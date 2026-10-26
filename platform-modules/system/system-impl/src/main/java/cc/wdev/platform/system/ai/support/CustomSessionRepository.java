package cc.wdev.platform.system.ai.support;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionIndexEntity;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionIndexService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.session.*;
import org.springframework.ai.session.support.SessionEventCodec;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static cc.wdev.platform.commons.ai.AiConstants.CHAT_CONTEXT_TENANT_ID;

/**
 * 基于 MyBatis-Plus 的 {@link SessionRepository} 实现，会话落在 {@code sys_ai_session}，事件落在 {@code sys_ai_session_event}。
 *
 * <h2>与 JdbcSessionRepository 的对应关系</h2>
 * <ul>
 * <li>事件顺序由 {@code sys_ai_session_event.id}（BIGSERIAL，单调递增）承担参考实现中 {@code seq} 的角色。</li>
 * <li>{@code SessionEvent#getId()} 映射为全局唯一的 {@code session_event_id}，行主键只负责事件顺序。</li>
 * <li>会话行 {@code event_version} 承担 CAS 版本号：appendEvent 成功追加和 appendEvents 成功后各递增一次。</li>
 * <li>压缩是追加 {@link EventType#COMPACTION} 边界事件，不修改或删除原事件；读取时由 {@link EventFilter} 投影活动视图。</li>
 * <li>过期会话通过单条 DELETE 清理，事件由外键级联删除；appendEvent 先锁定会话行，保证幂等检查的并发安全。</li>
 * </ul>
 *
 * @author elvea
 */
@Slf4j
public class CustomSessionRepository implements SessionRepository {

    private static final Set<String> INDEXED_ATTRIBUTES = Set.of(SessionAttributes.PRINCIPAL_NAME);

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    private final AiSessionIndexService aiSessionIndexService;

    private final TransactionTemplate transactionTemplate;

    private final SessionEventCodec codec = new SessionEventCodec();

    public CustomSessionRepository(AiSessionService aiSessionService,
                                   AiSessionEventService aiSessionEventService,
                                   AiSessionIndexService aiSessionIndexService,
                                   PlatformTransactionManager transactionManager) {
        Assert.notNull(aiSessionService, "aiSessionService must not be null");
        Assert.notNull(aiSessionEventService, "aiSessionEventService must not be null");
        Assert.notNull(aiSessionIndexService, "aiSessionIndexService must not be null");
        Assert.notNull(transactionManager, "transactionManager must not be null");
        this.aiSessionService = aiSessionService;
        this.aiSessionEventService = aiSessionEventService;
        this.aiSessionIndexService = aiSessionIndexService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // -------------------------------------------------------------------------
    // SessionRepository - Sessions
    // -------------------------------------------------------------------------

    @Override
    public @NonNull Session save(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");

        this.transactionTemplate.executeWithoutResult(_ -> {
            AiSessionEntity entity = this.aiSessionService.findBySessionId(session.id());
            if (entity == null) {
                entity = toSessionEntity(session);
            } else {
                entity.setExpiresAt(toUtc(session.expiresAt()));
                entity.setMetadata(this.codec.toJson(session.metadata()));
                String principalName = SessionAttributes.principalName(session);
                if (principalName != null) {
                    entity.setUserId(principalName);
                }
            }
            this.aiSessionService.save(entity);
            replaceSessionIndexes(session.id(), entity.getTenantId(), session.metadata());
        });
        return Objects.requireNonNull(findById(session.id()), () -> "Session vanished after save: " + session.id());
    }

    @Override
    public boolean saveIfAbsent(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");
        try {
            Boolean inserted = this.transactionTemplate.execute(_ -> {
                if (this.aiSessionService.findBySessionId(session.id()) != null) {
                    return false;
                }
                AiSessionEntity entity = toSessionEntity(session);
                this.aiSessionService.insert(entity);
                replaceSessionIndexes(session.id(), entity.getTenantId(), session.metadata());
                return true;
            });
            return Boolean.TRUE.equals(inserted);
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    @Override
    public @Nullable Session findById(@Nullable String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        return toSession(this.aiSessionService.findBySessionId(sessionId));
    }

    @Override
    public @NonNull List<Session> findByMetadata(@NonNull String name, @NonNull String value) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(value, "value must not be null");
        Set<String> sessionIds = this.aiSessionIndexService.findByAttrNameAndValue(name, value).stream()
            .map(AiSessionIndexEntity::getSessionId)
            .collect(java.util.stream.Collectors.toSet());
        if (sessionIds.isEmpty()) {
            return List.of();
        }
        List<AiSessionEntity> candidates = this.aiSessionService.findBySessionIds(sessionIds);
        return candidates.stream()
            .filter(entity -> Objects.equals(ActiveTypeEnum.ENABLED.getValue(), entity.getActive()))
            .map(this::toSession)
            .filter(Objects::nonNull)
            .filter(session -> session.metadata().containsKey(name)
                && value.equals(String.valueOf(session.metadata().get(name))))
            .sorted(Comparator.comparing(Session::createdAt).thenComparing(Session::id))
            .toList();
    }

    @Override
    public int deleteExpiredSessions(@NonNull Instant before) {
        Assert.notNull(before, "before must not be null");
        LocalDateTime cutoff = Objects.requireNonNull(toUtc(before), "expiration cutoff must not be null");
        int deleted = 0;
        for (AiSessionEntity session : this.aiSessionService.findExpiredSessions(cutoff)) {
            Boolean removed = this.transactionTemplate.execute(_ -> {
                // 先按 expires_at 条件删除会话，避免并发续期后误删子表数据。
                int count = this.aiSessionService.deleteExpiredSession(session.getSessionId(), cutoff);
                if (count > 0) {
                    this.aiSessionEventService.deleteBySessionId(session.getSessionId());
                    this.aiSessionIndexService.deleteBySessionId(session.getSessionId());
                }
                return count > 0;
            });
            if (Boolean.TRUE.equals(removed)) {
                deleted++;
            }
        }
        return deleted;
    }

    @Override
    public void delete(@NonNull String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        this.transactionTemplate.executeWithoutResult(_ -> {
            this.aiSessionService.deleteBySessionId(sessionId);
            this.aiSessionEventService.deleteBySessionId(sessionId);
            this.aiSessionIndexService.deleteBySessionId(sessionId);
        });
    }

    // -------------------------------------------------------------------------
    // SessionRepository - Events
    // -------------------------------------------------------------------------

    @Override
    public void appendEvent(@NonNull SessionEvent event) {
        Assert.notNull(event, "event must not be null");
        String sessionId = event.getSessionId();
        try {
            this.transactionTemplate.executeWithoutResult(_ -> {
                // UPDATE 先锁定会话行，并发 compaction 在这里串行化。
                int updated = this.aiSessionService.incrementEventVersion(sessionId);
                if (updated == 0) {
                    throw new IllegalArgumentException("Session not found: " + sessionId);
                }

                AiSessionEventEntity existing = this.aiSessionEventService.findBySessionEventId(event.getId());
                if (existing != null) {
                    if (!sessionId.equals(existing.getSessionId())) {
                        throw new IllegalStateException(eventIdCollisionMessage(event.getId()));
                    }
                    int decremented = this.aiSessionService.decrementEventVersion(sessionId);
                    Assert.state(decremented == 1, "Failed to undo event version increment for replay");
                    log.debug("appendEvent: event {} already exists for session {}; idempotent replay", event.getId(), sessionId);
                    return;
                }

                AiSessionEntity session = requireSessionExists(sessionId);
                this.aiSessionEventService.save(toEventEntity(event, session.getTenantId()));
            });
        } catch (DuplicateKeyException ex) {
            throw new IllegalStateException(eventIdCollisionMessage(event.getId()), ex);
        }
    }

    @Override
    public boolean appendEvents(@NonNull String sessionId, @NonNull List<SessionEvent> events, long expectedVersion) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        Assert.notEmpty(events, "events must not be empty");
        Set<String> batchIds = new HashSet<>();
        for (SessionEvent event : events) {
            Assert.isTrue(sessionId.equals(event.getSessionId()),
                () -> "Event '" + event.getId() + "' belongs to session '" + event.getSessionId() + "'");
            Assert.isTrue(batchIds.add(event.getId()),
                () -> "Event id '" + event.getId() + "' appears twice in the batch");
        }

        Boolean success = this.transactionTemplate.execute(_ -> {
            int updated = this.aiSessionService.casIncrementEventVersion(sessionId, expectedVersion);
            if (updated == 0) {
                if (this.aiSessionService.findBySessionId(sessionId) == null) {
                    throw new IllegalArgumentException("Session not found: " + sessionId);
                }
                return false;
            }

            List<AiSessionEventEntity> taken =
                this.aiSessionEventService.findBySessionEventIds(List.copyOf(batchIds));
            if (!taken.isEmpty()) {
                throw new IllegalArgumentException("Event id '" + taken.getFirst().getSessionEventId()
                    + "' is already in the log");
            }

            AiSessionEntity session = requireSessionExists(sessionId);
            this.aiSessionEventService.saveBatch(events.stream()
                .map(event -> toEventEntity(event, session.getTenantId()))
                .toList());
            return true;
        });
        return Boolean.TRUE.equals(success);
    }

    @Override
    public long getEventVersion(@NonNull String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        AiSessionEntity entity = this.aiSessionService.findBySessionId(sessionId);
        return entity != null && entity.getEventVersion() != null ? entity.getEventVersion() : 0L;
    }

    @Override
    public @NonNull List<SessionEvent> findEvents(@NonNull String sessionId, @NonNull EventFilter filter) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        Assert.notNull(filter, "filter must not be null");

        List<SessionEvent> events = this.aiSessionEventService.findBySessionId(sessionId).stream()
            .map(this::toSessionEvent)
            .toList();
        // activeView、turn 补齐和窗口语义统一由框架参考实现处理，避免两套规则漂移。
        return filter.apply(events);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private AiSessionEntity requireSessionExists(String sessionId) {
        AiSessionEntity entity = this.aiSessionService.findBySessionId(sessionId);
        if (entity == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        return entity;
    }

    private static String eventIdCollisionMessage(String eventId) {
        return "Event id '" + eventId + "' is already used by another session; event ids must be unique across sessions";
    }

    private Long resolveTenantId(Session session) {
        return MapUtils.getLong(session.metadata(), CHAT_CONTEXT_TENANT_ID, TenantContext.getTenantId());
    }

    private String resolvePrincipalName(Session session) {
        String principalName = SessionAttributes.principalName(session);
        return principalName != null ? principalName : "";
    }

    private void replaceSessionIndexes(String sessionId, @Nullable Long tenantId, Map<String, Object> metadata) {
        this.aiSessionIndexService.replaceSessionIndexes(
            sessionId, tenantId, metadata, INDEXED_ATTRIBUTES);
    }

    private AiSessionEntity toSessionEntity(Session session) {
        AiSessionEntity entity = new AiSessionEntity();
        entity.setCreatedAt(toUtc(session.createdAt()));
        entity.setEventVersion(0L);
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        entity.setTenantId(resolveTenantId(session));
        entity.setSessionId(session.id());
        entity.setUserId(resolvePrincipalName(session));
        entity.setMetadata(this.codec.toJson(session.metadata()));
        entity.setExpiresAt(toUtc(session.expiresAt()));
        return entity;
    }

    @Nullable
    private Session toSession(@Nullable AiSessionEntity entity) {
        if (entity == null) {
            return null;
        }

        Map<String, Object> metadata = new HashMap<>(this.codec.fromJsonMap(entity.getMetadata()));
        if (!metadata.containsKey(SessionAttributes.PRINCIPAL_NAME) && entity.getUserId() != null) {
            metadata.put(SessionAttributes.PRINCIPAL_NAME, entity.getUserId());
        }

        Session.Builder builder = Session.builder()
            .id(entity.getSessionId())
            .createdAt(Objects.requireNonNull(fromUtc(entity.getCreatedAt()), "session.createdAt must not be null"))
            .metadata(metadata);
        if (entity.getExpiresAt() != null) {
            builder.expiresAt(fromUtc(entity.getExpiresAt()));
        }
        return builder.build();
    }

    private SessionEvent toSessionEvent(AiSessionEventEntity entity) {
        EventType eventType = entity.getEventType() == null || entity.getEventType().isBlank()
            ? EventType.MESSAGE
            : EventType.of(entity.getEventType());
        MessageType messageType = entity.getMessageType() == null || entity.getMessageType().isBlank()
            ? null
            : MessageType.valueOf(entity.getMessageType());
        SessionEvent.Builder builder = this.codec.decode(new SessionEventCodec.EncodedEvent(
            eventType,
            messageType,
            entity.getMessageContent(),
            entity.getPayload() != null ? entity.getPayload() : ""));

        Map<String, Object> metadata = new HashMap<>(this.codec.fromJsonMap(entity.getMetadata()));
        if (entity.getSynthetic() != null && entity.getSynthetic() == 1) {
            metadata.put(SessionEvent.METADATA_SYNTHETIC, true);
        }
        return builder
            .id(entity.getSessionEventId() != null ? entity.getSessionEventId() : String.valueOf(entity.getId()))
            .sessionId(entity.getSessionId())
            .timestamp(Objects.requireNonNull(fromUtc(entity.getTimestamp()), "event.timestamp must not be null"))
            .metadata(metadata)
            .build();
    }

    private AiSessionEventEntity toEventEntity(SessionEvent event, @Nullable Long tenantId) {
        SessionEventCodec.EncodedEvent encoded = this.codec.encode(event);
        AiSessionEventEntity entity = new AiSessionEventEntity();
        entity.setTenantId(tenantId);
        entity.setSessionId(event.getSessionId());
        entity.setSessionEventId(event.getId());
        entity.setEventType(encoded.type().name());
        entity.setTimestamp(toUtc(event.getTimestamp()));
        entity.setMessageType(encoded.messageType() != null ? encoded.messageType().name() : null);
        entity.setMessageContent(encoded.text());
        entity.setPayload(encoded.payload());
        entity.setSynthetic(event.isSynthetic() ? ActiveTypeEnum.ENABLED.getValue() : ActiveTypeEnum.DISABLED.getValue());
        entity.setArchived(ActiveTypeEnum.DISABLED.getValue());
        entity.setMetadata(this.codec.toJson(event.getMetadata()));
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        return entity;
    }

    @Nullable
    private static LocalDateTime toUtc(@Nullable Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    @Nullable
    private static Instant fromUtc(@Nullable LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }

}
