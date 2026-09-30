package cc.wdev.platform.system.ai.support;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.session.EventFilter;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionEvent;
import org.springframework.ai.session.SessionRepository;
import org.springframework.ai.session.compaction.CompactionPlan;
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
 * <li>会话行 {@code event_version} 承担 CAS 版本号：appendEvent 成功追加和 applyCompaction 成功时各递增一次。</li>
 * <li>过期会话通过单条 DELETE 清理，事件由外键级联删除；appendEvent 先锁定会话行，保证幂等检查和 compaction 的并发安全。</li>
 * </ul>
 *
 * @author elvea
 */
@Slf4j
public class CustomSessionRepository implements SessionRepository {

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    private final TransactionTemplate transactionTemplate;

    private final SessionEventCodec codec = new SessionEventCodec();

    public CustomSessionRepository(AiSessionService aiSessionService,
                                   AiSessionEventService aiSessionEventService,
                                   PlatformTransactionManager transactionManager) {
        Assert.notNull(aiSessionService, "aiSessionService must not be null");
        Assert.notNull(aiSessionEventService, "aiSessionEventService must not be null");
        Assert.notNull(transactionManager, "transactionManager must not be null");
        this.aiSessionService = aiSessionService;
        this.aiSessionEventService = aiSessionEventService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // -------------------------------------------------------------------------
    // SessionRepository - Session lifecycle
    // -------------------------------------------------------------------------

    @Override
    public @NonNull Session save(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");

        AiSessionEntity entity = this.aiSessionService.findBySessionId(session.id());
        if (entity == null) {
            entity = toSessionEntity(session);
        } else {
            entity.setExpiresAt(toUtc(session.expiresAt()));
            entity.setMetadata(this.codec.toJson(session.metadata()));
            entity.setUserId(session.userId());
        }
        this.aiSessionService.save(entity);
        return Objects.requireNonNull(findById(session.id()), () -> "Session vanished after save: " + session.id());
    }

    @Override
    public boolean saveIfAbsent(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");
        if (this.aiSessionService.findBySessionId(session.id()) != null) {
            return false;
        }
        this.aiSessionService.insert(toSessionEntity(session));
        return true;
    }

    @Override
    public @Nullable Session findById(@Nullable String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        return toSession(this.aiSessionService.findBySessionId(sessionId));
    }

    @Override
    public @NonNull List<Session> findByUserId(@NonNull String userId) {
        Assert.hasText(userId, "userId must not be null or empty");
        return this.aiSessionService.findByUserId(userId).stream().map(this::toSession).toList();
    }

    @Override
    public int deleteExpiredSessions(@NonNull Instant before) {
        Assert.notNull(before, "before must not be null");
        return this.aiSessionService.deleteExpiredSessions(toUtc(before));
    }

    @Override
    public void delete(@NonNull String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        this.aiSessionService.deleteBySessionId(sessionId);
    }

    // -------------------------------------------------------------------------
    // SessionRepository — event log
    // -------------------------------------------------------------------------

    @Override
    public void appendEvent(@NonNull SessionEvent event) {
        Assert.notNull(event, "event must not be null");
        String sessionId = event.getSessionId();
        try {
            this.transactionTemplate.executeWithoutResult(_ -> {
                // UPDATE 先锁定会话行，既有并发 compaction 在这里串行化，事件序号也不会越过尚未提交的 compaction 尾部。
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
    public boolean applyCompaction(@NonNull String sessionId, @NonNull CompactionPlan plan, long expectedVersion) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        Assert.notNull(plan, "plan must not be null");
        AiSessionEntity session = requireSessionExists(sessionId);
        Boolean success = this.transactionTemplate.execute(_ -> {
            if (this.aiSessionService.casIncrementEventVersion(sessionId, expectedVersion) == 0) {
                return false;
            }
            archiveInPlace(sessionId, plan.archiveIds());
            insertEvents(sessionId, session.getTenantId(), plan.inserts());
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

        // Pattern 与多关键词保留在内存中执行；其余条件尽量下推，窗口也随之下推。
        boolean inMemoryWindow = filter.pattern() != null || filter.keywords() != null;
        List<AiSessionEventEntity> entities = this.aiSessionEventService.findEvents(
            sessionId, inMemoryWindow ? filter.withoutWindow() : filter);
        List<SessionEvent> events = entities.stream().map(this::toSessionEvent).toList();

        if (inMemoryWindow) {
            return filter.apply(events);
        }

        if (filter.lastN() != null) {
            List<SessionEvent> window = new ArrayList<>(events);
            Collections.reverse(window);
            return Collections.unmodifiableList(extendToTurnStart(sessionId, filter, window));
        }
        return events;
    }

    @Override
    public @NonNull List<SessionEvent> findEventsByUserId(@NonNull String userId, @NonNull EventFilter filter) {
        Assert.hasText(userId, "userId must not be null or empty");
        Assert.notNull(filter, "filter must not be null");
        EventFilter perSession = filter.withoutWindow();
        List<SessionEvent> matches = new ArrayList<>();
        for (Session session : findByUserId(userId)) {
            matches.addAll(findEvents(session.id(), perSession));
        }
        matches.sort(Comparator.comparing(SessionEvent::getTimestamp).thenComparing(SessionEvent::getId));
        return filter.applyWindow(matches);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void archiveInPlace(String sessionId, Set<String> archiveIds) {
        if (archiveIds.isEmpty()) {
            return;
        }

        this.aiSessionEventService.archiveByIds(sessionId, List.copyOf(archiveIds));
    }

    /**
     * 将每个新事件组插入锚点之前；无锚点的组追加到末尾。顺序列由数据库生成，因此从最小锚点开始的尾部会重写，
     * 与 {@link org.springframework.ai.session.jdbc.JdbcSessionRepository} 的行为保持一致。
     */
    private void insertEvents(String sessionId, @Nullable Long tenantId, List<CompactionPlan.Insert> inserts) {
        if (inserts.isEmpty()) {
            return;
        }

        Map<String, List<SessionEvent>> insertBefore = new LinkedHashMap<>();
        List<SessionEvent> append = new ArrayList<>();
        Map<String, Long> anchorSequences = new LinkedHashMap<>();
        for (CompactionPlan.Insert insert : inserts) {
            for (SessionEvent event : insert.events()) {
                Assert.isTrue(sessionId.equals(event.getSessionId()),
                    "Compaction insert contains an event for another session");
            }
            if (insert.isAppend()) {
                append.addAll(insert.events());
                continue;
            }

            String anchorId = insert.beforeEventId();
            AiSessionEventEntity anchor = this.aiSessionEventService.findBySessionEventId(anchorId);
            if (anchor == null || !sessionId.equals(anchor.getSessionId())) {
                throw new IllegalArgumentException(
                    "inserts refers to an anchor event that is not in the log of session " + sessionId);
            }
            anchorSequences.put(anchorId, anchor.getId());
            insertBefore.computeIfAbsent(anchorId, _ -> new ArrayList<>()).addAll(insert.events());
        }

        List<SessionEvent> toInsert = new ArrayList<>();
        if (!anchorSequences.isEmpty()) {
            long fromSequence = anchorSequences.values().stream().mapToLong(Long::longValue).min().orElseThrow();
            List<AiSessionEventEntity> tail = this.aiSessionEventService.findFromSequence(sessionId, fromSequence);
            this.aiSessionEventService.deleteFromSequence(sessionId, fromSequence);
            for (AiSessionEventEntity entity : tail) {
                SessionEvent event = toSessionEvent(entity);
                toInsert.addAll(insertBefore.getOrDefault(event.getId(), List.of()));
                toInsert.add(event);
            }
        }
        toInsert.addAll(append);
        saveEvents(tenantId, toInsert);
    }

    /**
     * lastN 窗口落在某个 turn 中间时，向前补齐到该 turn 的起点（USER 事件），
     * 避免只返回工具调用而没有它所属的用户消息。
     */
    private List<SessionEvent> extendToTurnStart(String sessionId, EventFilter filter, List<SessionEvent> window) {
        Integer lastN = filter.lastN();
        if (lastN == null || window.isEmpty() || window.size() < lastN || window.getFirst().isTurnStart()
            || filter.hasTextCriteria()
            || (filter.messageTypes() != null && !filter.messageTypes().contains(MessageType.USER))) {
            return window;
        }
        List<AiSessionEventEntity> head = this.aiSessionEventService.findTurnHead(
            sessionId, window.getFirst().getId(), filter);
        if (head.isEmpty()) {
            return window;
        }
        List<SessionEvent> extended = new ArrayList<>(head.size() + window.size());
        head.stream().map(this::toSessionEvent).forEach(extended::add);
        extended.addAll(window);
        return extended;
    }

    private AiSessionEntity requireSessionExists(String sessionId) {
        AiSessionEntity entity = this.aiSessionService.findBySessionId(sessionId);
        if (entity == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        return entity;
    }

    private void saveEvents(@Nullable Long tenantId, List<SessionEvent> events) {
        if (events.isEmpty()) {
            return;
        }
        this.aiSessionEventService.saveBatch(events.stream().map(event -> toEventEntity(event, tenantId)).toList());
    }

    private static String eventIdCollisionMessage(String eventId) {
        return "Event id '" + eventId + "' is already used by another session; event ids must be unique across sessions";
    }

    private Long resolveTenantId(Session session) {
        return MapUtils.getLong(session.metadata(), CHAT_CONTEXT_TENANT_ID, TenantContext.getTenantId());
    }

    private AiSessionEntity toSessionEntity(Session session) {
        AiSessionEntity entity = new AiSessionEntity();
        entity.setCreatedAt(toUtc(session.createdAt()));
        entity.setEventVersion(0L);
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        entity.setTenantId(resolveTenantId(session));
        entity.setSessionId(session.id());
        entity.setUserId(session.userId());
        entity.setMetadata(this.codec.toJson(session.metadata()));
        entity.setExpiresAt(toUtc(session.expiresAt()));
        return entity;
    }

    @Nullable
    private Session toSession(@Nullable AiSessionEntity entity) {
        if (entity == null) {
            return null;
        }

        Session.Builder builder = Session.builder()
            .id(entity.getSessionId())
            .userId(entity.getUserId() != null ? entity.getUserId() : "")
            .createdAt(Objects.requireNonNull(fromUtc(entity.getCreatedAt()), "session.createdAt must not be null"))
            .metadata(this.codec.fromJsonMap(entity.getMetadata()));
        if (entity.getExpiresAt() != null) {
            builder.expiresAt(fromUtc(entity.getExpiresAt()));
        }
        return builder.build();
    }

    private SessionEvent toSessionEvent(AiSessionEventEntity entity) {
        Message message = this.codec.decode(new SessionEventCodec.EncodedMessage(
            MessageType.valueOf(entity.getMessageType()), entity.getMessageContent(), entity.getMessageData()));
        Map<String, Object> metadata = new HashMap<>(this.codec.fromJsonMap(entity.getMetadata()));
        if (entity.getSynthetic() != null && entity.getSynthetic() == 1) {
            metadata.put(SessionEvent.METADATA_SYNTHETIC, true);
        }
        return SessionEvent.builder()
            .id(entity.getSessionEventId() != null ? entity.getSessionEventId() : String.valueOf(entity.getId()))
            .sessionId(entity.getSessionId())
            .timestamp(Objects.requireNonNull(fromUtc(entity.getTimestamp()), "event.timestamp must not be null"))
            .message(message)
            .archived(entity.getArchived() != null && entity.getArchived() == 1)
            .metadata(metadata)
            .build();
    }

    private AiSessionEventEntity toEventEntity(SessionEvent event, @Nullable Long tenantId) {
        SessionEventCodec.EncodedMessage message = this.codec.encode(event.getMessage());
        AiSessionEventEntity entity = new AiSessionEventEntity();
        entity.setTenantId(tenantId);
        entity.setSessionId(event.getSessionId());
        entity.setSessionEventId(event.getId());
        entity.setTimestamp(toUtc(event.getTimestamp()));
        entity.setMessageType(message.type().name());
        entity.setMessageContent(message.text());
        entity.setMessageData(message.data());
        entity.setSynthetic(event.isSynthetic() ? ActiveTypeEnum.ENABLED.getValue() : ActiveTypeEnum.DISABLED.getValue());
        entity.setArchived(event.isArchived() ? ActiveTypeEnum.ENABLED.getValue() : ActiveTypeEnum.DISABLED.getValue());
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
