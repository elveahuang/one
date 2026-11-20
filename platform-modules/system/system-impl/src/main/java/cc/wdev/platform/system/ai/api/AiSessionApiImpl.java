package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AiSessionApiImpl implements AiSessionApi {

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    /**
     * @see AiSessionApi#findHistory
     */
    @Override
    public Page<AiSessionEventVo> findHistory(AiSessionEventRequest request) {
        return aiSessionEventService.findHistory(request);
    }

    /**
     * @see AiSessionApi#findCurrent
     */
    @Override
    public Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request) {
        return aiSessionEventService.findCurrent(request);
    }

    /**
     * @see AiSessionApi#resolveGreetingEvent(AiSessionEventSaveRequest)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resolveGreetingEvent(AiSessionEventSaveRequest request) {
        // 检查对话是否存在
        AiSessionEntity session = StringUtils.isNotEmpty(request.getSessionId())
            ? aiSessionService.getSession(request.getSessionId(), request.getUserId(), request.getTenantId())
            : null;

        if (session == null && StringUtils.isEmpty(request.getSessionId())) {
            Page<AiSessionEntity> latestPage = aiSessionService.findPageByUserId(
                String.valueOf(request.getUserId()), request.getTenantId(), request.getBizType(), PageRequest.of(0, 1));
            if (latestPage != null && !latestPage.isEmpty()) {
                session = latestPage.getContent().getFirst();
                request.setSessionId(session.getSessionId());
            }
        }

        if (session == null) {
            // 会话不存在或归属不符时一律换新会话ID，沿用传入ID直接落库会撞会话唯一索引
            request.setSessionId(StringUtils.uuid());
            AiSessionEntity newSession = new AiSessionEntity();
            newSession.setTenantId(request.getTenantId());
            newSession.setUserId(String.valueOf(request.getUserId()));
            newSession.setSessionId(request.getSessionId());
            newSession.setBizType(request.getBizType());
            newSession.setChatType(request.getChatType());
            newSession.setEventVersion(0L);
            newSession.setMetadata("{}");
            newSession.setActive(ActiveTypeEnum.ENABLED.getValue());
            session = aiSessionService.save(newSession);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        String sessionId = session.getSessionId();
        String sessionEventId = String.format("greeting_%s_%s", sessionId, today);
        if (!aiSessionEventService.hasEventSince(sessionId, todayStart)
            && aiSessionEventService.findBySessionEventId(sessionEventId) == null) {
            Map<String, Object> eventPayload = new LinkedHashMap<>();
            eventPayload.put("type", "greeting");
            eventPayload.put("template", request.getTemplate());
            eventPayload.put("data", StringUtils.isNotEmpty(request.getPayload())
                ? GsonUtils.toObjectMap(request.getPayload())
                : Map.of());

            AiSessionEventEntity eventEntity = new AiSessionEventEntity();
            eventEntity.setTenantId(session.getTenantId());
            eventEntity.setSessionId(sessionId);
            eventEntity.setSessionEventId(sessionEventId);
            eventEntity.setEventType("GREETING");
            eventEntity.setTimestamp(LocalDateTime.now());
            eventEntity.setMessageType("ASSISTANT");
            eventEntity.setMessageContent("");
            eventEntity.setPayload(GsonUtils.toJson(eventPayload));
            eventEntity.setSynthetic(ActiveTypeEnum.ENABLED.getValue());
            eventEntity.setArchived(ActiveTypeEnum.DISABLED.getValue());
            eventEntity.setMetadata("{\"uiState\":null}");
            eventEntity.setActive(ActiveTypeEnum.ENABLED.getValue());
            try {
                aiSessionEventService.save(eventEntity);
            } catch (DuplicateKeyException e) {
                log.warn("Duplicate greeting event skipped: {}", sessionEventId);
            }
        }
        return sessionId;
    }

    /**
     * @see AiSessionApi#updateEventMetadata
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEventMetadata(Long tenantId, Long userId, String sessionId, String eventId, String metadataJson) {
        if (!ObjectUtils.isValidId(userId) || StringUtils.isEmpty(eventId)) {
            return false;
        }
        AiSessionEventEntity event = aiSessionEventService.findBySessionEventId(eventId);
        if (event == null) {
            try {
                Long numericId = Long.parseLong(eventId);
                event = aiSessionEventService.findById(numericId);
            } catch (NumberFormatException ignored) {
            }
        }
        if (event == null) {
            return false;
        }
        String targetSessionId = StringUtils.isNotEmpty(sessionId) ? sessionId : event.getSessionId();
        AiSessionEntity session = aiSessionService.getSession(
            targetSessionId, userId, ObjectUtils.isValidId(tenantId) ? tenantId : null);
        if (session == null || !session.getSessionId().equals(event.getSessionId())) {
            return false;
        }
        event.setMetadata(StringUtils.nvl(metadataJson, "{}"));
        aiSessionEventService.save(event);
        return true;
    }

}
