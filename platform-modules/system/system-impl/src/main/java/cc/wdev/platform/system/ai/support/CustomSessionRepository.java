package cc.wdev.platform.system.ai.support;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.session.EventFilter;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionEvent;
import org.springframework.ai.session.SessionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.ai.AiConstants.CAHT_CONTEXT_TENANT_ID_KEY;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class CustomSessionRepository implements SessionRepository {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    // -------------------------------------------------------------------------
    // SessionRepository — session lifecycle
    // -------------------------------------------------------------------------

    @Override
    public @NonNull Session save(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");

        // 获取元数据
        Map<String, Object> metadata = session.metadata();
        Long tenantId = MapUtils.getLong(metadata, CAHT_CONTEXT_TENANT_ID_KEY, TenantContext.getTenantId());

        // 创建或者更新会话信息
        AiSessionEntity entity = this.aiSessionService.findBySessionId(session.id());
        if (entity == null) {
            entity = new AiSessionEntity();
        }
        entity.setTenantId(tenantId);
        entity.setSessionId(session.id());
        entity.setUserId(session.userId());
        entity.setMetadata(toJson(session.metadata()));
        entity.setExpiresAt(session.expiresAt() != null ? LocalDateTime.ofInstant(session.expiresAt(), ZoneOffset.UTC) : null);

        return toSession(aiSessionService.save(entity));
    }

    @Override
    public @Nullable Session findById(@Nullable String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        AiSessionEntity entity = aiSessionService.findBySessionId(sessionId);
        return entity == null ? null : toSession(entity);
    }

    @Override
    public @NonNull List<Session> findByUserId(@NonNull String userId) {
        Assert.hasText(userId, "userId must not be null or empty");
        List<AiSessionEntity> list = aiSessionService.findByUserId(userId);
        return list.stream().map(this::toSession).collect(Collectors.toList());
    }

    @Override
    public @NonNull List<String> findExpiredSessionIds(@NonNull Instant before) {
        Assert.notNull(before, "before must not be null");
        LocalDateTime ts = LocalDateTime.ofInstant(before, ZoneOffset.UTC);
        List<AiSessionEntity> list = aiSessionService.findExpiredSessions(ts);
        return list.stream().map(e -> String.valueOf(e.getSessionId())).collect(Collectors.toList());
    }

    @Override
    public void delete(@NonNull String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        aiSessionEventService.deleteBySessionId(sessionId);
        aiSessionService.deleteBySessionId(sessionId);
    }

    // -------------------------------------------------------------------------
    // SessionRepository — event log
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void appendEvent(@NonNull SessionEvent event) {
        Assert.notNull(event, "event must not be null");
        AiSessionEntity sessionEntity = this.getSession(event.getSessionId());
        this.aiSessionEventService.save(toEventEntity(event, event.getSessionId()));
        // 推进版本
        sessionEntity.setEventVersion(sessionEntity.getEventVersion() + 1);
        this.aiSessionService.updateById(sessionEntity);
    }

    @Override
    public boolean compactEvents(@NonNull String sessionId,
                                 @NonNull List<SessionEvent> archivedEvents,
                                 @NonNull List<SessionEvent> retainedEvents,
                                 long expectedVersion) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        AiSessionEntity sessionEntity = this.getSession(sessionId);
        List<AiSessionEventEntity> activeEvents = this.aiSessionEventService.findActiveBySessionId(sessionId);
        if (activeEvents.size() <= retainedEvents.size()) {
            return this.aiSessionService.incrementEventVersionIfMatch(sessionEntity.getId(), expectedVersion) > 0;
        }
        if (!archivedEvents.isEmpty()) {
            List<Long> archivedIds = archivedEvents.stream().map(e -> Long.parseLong(e.getId())).collect(Collectors.toList());
            this.aiSessionEventService.archiveByIds(archivedIds);
        }
        List<AiSessionEventEntity> retainedEntities = retainedEvents.stream().map(this::toEventEntity).collect(Collectors.toList());
        this.aiSessionEventService.replaceActiveWindow(sessionId, retainedEntities);
        return this.aiSessionService.incrementEventVersionIfMatch(sessionEntity.getId(), expectedVersion) > 0;
    }

    @Override
    public long getEventVersion(@NonNull String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        AiSessionEntity entity = this.aiSessionService.findBySessionId(sessionId);
        return (entity != null && entity.getEventVersion() != null) ? entity.getEventVersion() : 0L;
    }

    @Override
    public @NonNull List<SessionEvent> findEvents(@NonNull String sessionId, @NonNull EventFilter filter) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        Assert.notNull(filter, "filter must not be null");
        List<AiSessionEventEntity> list = this.aiSessionEventService.findEvents(sessionId, filter);
        List<SessionEvent> result = list.stream().map(this::toSessionEvent).collect(Collectors.toList());
        if ((filter.keywords() != null && !filter.keywords().isEmpty()) || filter.pattern() != null) {
            result = result.stream().filter(filter::matches).collect(Collectors.toList());
        }
        if (filter.lastN() != null) {
            Collections.reverse(result);
        }
        return Collections.unmodifiableList(result);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private AiSessionEntity getSession(String sessionId) {
        AiSessionEntity entity = this.aiSessionService.findBySessionId(sessionId);
        if (entity == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        return entity;
    }

    private Session toSession(AiSessionEntity e) {
        Session.Builder builder = Session.builder()
            .id(String.valueOf(e.getSessionId()))
            .userId(String.valueOf(e.getUserId() != null ? e.getUserId() : 0L))
            .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toInstant(ZoneOffset.UTC) : Instant.now())
            .metadata(fromJson(e.getMetadata()));
        if (e.getExpiresAt() != null) {
            builder.expiresAt(e.getExpiresAt().toInstant(ZoneOffset.UTC));
        }
        return builder.build();
    }

    private SessionEvent toSessionEvent(AiSessionEventEntity e) {
        MessageType type = MessageType.valueOf(e.getMessageType());
        Message message = toMessage(type, e.getMessageContent(), e.getMessageData());
        Map<String, Object> metadata = new HashMap<>(fromJson(e.getMetadata()));
        if (e.getSynthetic() != null && e.getSynthetic() == 1) {
            metadata.put(SessionEvent.METADATA_SYNTHETIC, true);
        }
        SessionEvent.Builder builder = SessionEvent.builder()
            .id(String.valueOf(e.getId()))
            .sessionId(String.valueOf(e.getSessionId()))
            .timestamp(e.getTimestamp() != null ? e.getTimestamp().toInstant(ZoneOffset.UTC) : Instant.now())
            .message(message)
            .metadata(metadata);
        if (e.getBranch() != null) {
            builder.branch(e.getBranch());
        }
        return builder.build();
    }

    private AiSessionEventEntity toEventEntity(SessionEvent event) {
        return toEventEntity(event, event.getSessionId());
    }

    private AiSessionEventEntity toEventEntity(SessionEvent event, String sessionId) {
        Message msg = event.getMessage();

        AiSessionEventEntity entity = new AiSessionEventEntity();
        entity.setTenantId(TenantContext.getTenantId());
        entity.setSessionId(sessionId);
        entity.setTimestamp(LocalDateTime.ofInstant(event.getTimestamp(), ZoneOffset.UTC));
        entity.setMessageType(msg.getMessageType().name());
        entity.setMessageContent(msg.getText());
        entity.setMessageData(messageDataToJson(msg));
        entity.setSynthetic(event.isSynthetic() ? 1 : 0);
        entity.setBranch(event.getBranch());
        entity.setMetadata(toJson(event.getMetadata()));
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        return entity;
    }

    private Message toMessage(MessageType type, @Nullable String content, @Nullable String messageData) {
        return switch (type) {
            case USER -> new UserMessage(content != null ? content : "");
            case SYSTEM -> new SystemMessage(content != null ? content : "");
            case ASSISTANT -> {
                if (messageData != null && !messageData.isBlank()) {
                    List<AssistantMessage.ToolCall> toolCalls = parseToolCalls(messageData);
                    yield AssistantMessage.builder().content(content).toolCalls(toolCalls).build();
                }
                yield new AssistantMessage(content != null ? content : "");
            }
            case TOOL -> {
                if (messageData != null && !messageData.isBlank()) {
                    List<ToolResponseMessage.ToolResponse> responses = parseToolResponses(messageData);
                    yield ToolResponseMessage.builder().responses(responses).build();
                }
                yield ToolResponseMessage.builder().responses(List.of()).build();
            }
        };
    }

    @Nullable
    private String messageDataToJson(Message message) {
        if (message instanceof AssistantMessage am && am.hasToolCalls()) {
            return toJson(am.getToolCalls());
        }
        if (message instanceof ToolResponseMessage trm) {
            return toJson(trm.getResponses());
        }
        return null;
    }

    private List<AssistantMessage.ToolCall> parseToolCalls(String json) {
        try {
            return JSON_MAPPER.readValue(json, new TypeReference<List<AssistantMessage.ToolCall>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to deserialize tool calls from JSON; returning empty list", e);
            return List.of();
        }
    }

    private List<ToolResponseMessage.ToolResponse> parseToolResponses(String json) {
        try {
            return JSON_MAPPER.readValue(json, new TypeReference<List<ToolResponseMessage.ToolResponse>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to deserialize tool responses from JSON; returning empty list", e);
            return List.of();
        }
    }

    @Nullable
    private String toJson(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        try {
            return JSON_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            log.warn("Failed to serialize value to JSON", e);
            return null;
        }
    }

    private Map<String, Object> fromJson(@Nullable String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return JSON_MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("Failed to deserialize metadata JSON; returning empty map", e);
            return new HashMap<>();
        }
    }

}
