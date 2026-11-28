package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.ai.domain.converter.AiSessionEventConverter;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.repository.AiSessionEventRepository;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.session.EventType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSessionEventServiceImpl
    extends BaseCachingEntityService<AiSessionEventEntity, Long, AiSessionEventRepository>
    implements AiSessionEventService {

    private final AiSessionService aiSessionService;

    /**
     * @see AiSessionEventService#deleteBySessionId(String)
     */
    @Override
    public void deleteBySessionId(String sessionId) {
        this.lambdaUpdateWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .remove();
    }

    /**
     * @see AiSessionEventService#findBySessionId(String)
     */
    @Override
    public List<AiSessionEventEntity> findBySessionId(String sessionId) {
        if (ObjectUtils.isEmpty(sessionId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            // 仅供界面回放的自定义事件不能进入模型日志：EventFilter.lastN 不限类型，
            // 无 message 的事件会占窗口且 getMessage() 会抛异常
            .notIn(AiSessionEventEntity::getEventType, List.of("GREETING", "QUESTION"))
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiSessionEventEntity::getId)
            .list();
    }

    /**
     * @see AiSessionEventService#findUserMessagesBySessionIds(List)
     */
    @Override
    public List<AiSessionEventEntity> findUserMessagesBySessionIds(List<String> sessionIds) {
        if (ObjectUtils.isEmpty(sessionIds)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .in(AiSessionEventEntity::getSessionId, sessionIds)
            .eq(AiSessionEventEntity::getEventType, EventType.MESSAGE.name())
            .eq(AiSessionEventEntity::getMessageType, "USER")
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiSessionEventEntity::getId)
            .list();
    }

    /**
     * @see AiSessionEventService#findBySessionEventId(String)
     */
    @Override
    public AiSessionEventEntity findBySessionEventId(String sessionEventId) {
        if (ObjectUtils.isEmpty(sessionEventId)) {
            return null;
        }

        return this.findOne(lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionEventId, sessionEventId));
    }

    /**
     * @see AiSessionEventService#findBySessionEventIds(List)
     */
    @Override
    public List<AiSessionEventEntity> findBySessionEventIds(List<String> sessionEventIds) {
        if (ObjectUtils.isEmpty(sessionEventIds)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .in(AiSessionEventEntity::getSessionEventId, sessionEventIds)
            .list();
    }

    /**
     * @see AiSessionEventService#findHistory(AiSessionEventRequest)
     */
    @Override
    public Page<AiSessionEventVo> findHistory(AiSessionEventRequest request) {
        IPage<AiSessionEventVo> page = this.mapper.findTurnHead(getMyBatisPlusPage(request), request.getUserId(), request.getId());
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), page.getRecords(), page.getTotal());
    }

    /**
     * @see AiSessionEventService#findCurrent(AiSessionEventRequest)
     */
    @Override
    public Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request) {
        if (ObjectUtils.isEmpty(request.getSessionId())) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        // 归属校验：带用户条件时会话必须是本人的，否则返回空页（不暴露他人会话内容）
        if (ObjectUtils.isValidId(request.getUserId())) {
            AiSessionEntity session = this.aiSessionService.getSession(
                request.getSessionId(), request.getUserId(),
                ObjectUtils.isValidId(request.getTenantId()) ? request.getTenantId() : null);
            if (session == null) {
                log.warn("findCurrent ownership mismatch: sessionId={}, userId={}", request.getSessionId(), request.getUserId());
                return SpringDataUtils.emptyPage(request.getPageable());
            }
        }
        IPage<AiSessionEventEntity> page = lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, request.getSessionId())
            // QUESTION/ANSWER 为界面问答卡片事件，messageContent 为空，需与 GREETING 一并放行
            .in(AiSessionEventEntity::getEventType,
                List.of(EventType.MESSAGE.name(), "GREETING", "QUESTION", "ANSWER"))
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(List.of(AiSessionEventEntity::getCreatedAt, AiSessionEventEntity::getId))
            .page(getMyBatisPlusPage(request));

        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(),
            page.getRecords().stream().map(AiSessionEventConverter.INSTANCE::entity2Vo).toList(),
            page.getTotal());
    }

    /**
     * @see AiSessionEventService#hasEventSince(String, java.time.LocalDateTime)
     */
    @Override
    public boolean hasEventSince(String sessionId, java.time.LocalDateTime since) {
        if (ObjectUtils.isEmpty(sessionId) || since == null) {
            return false;
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .ge(AiSessionEventEntity::getCreatedAt, since)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

}
