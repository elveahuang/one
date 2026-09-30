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
import org.springframework.ai.session.compaction.CompactionPlan;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.ai.AiConstants.CAHT_CONTEXT_TENANT_ID_KEY;

/**
 * 基于 MyBatis-Plus 的 {@link SessionRepository} 实现，会话落在 {@code sys_ai_session}，事件落在 {@code sys_ai_session_event}。
 *
 * <h2>与 JdbcSessionRepository 的对应关系</h2>
 * <ul>
 * <li>事件顺序由 {@code sys_ai_session_event.id}（BIGSERIAL，单调递增）承担参考实现中 {@code seq} 的角色，
 * 因此查询一律按 id 升序，lastN 取倒序前 N 再翻回正序。</li>
 * <li>{@code SessionEvent#getId()} 落库时映射为事件行的主键，读取时映射回 {@code String.valueOf(id)}。</li>
 * <li>会话行 {@code event_version} 承担 CAS 版本号：每次 appendEvent / 成功的 applyCompaction 递增。</li>
 * </ul>
 *
 * <h2>已知限制</h2>
 * <ul>
 * <li>{@code sys_ai_session_event} 没有独立的 event id 列，无法在写入前判断事件是否已存在，
 * 因此 {@link #appendEvent(SessionEvent)} 做不到按事件 id 幂等（重复调用会追加重复行并递增版本）。
 * 需要完整幂等时，给事件表加 {@code event_id} 唯一列后在此处先查后写。</li>
 * <li>{@link #saveIfAbsent(Session)} 为先查后插，未依赖唯一约束，极端并发下仍可能重复插入。</li>
 * </ul>
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class CustomSessionRepository implements SessionRepository {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    // -------------------------------------------------------------------------
    // SessionRepository — Session lifecycle
    // -------------------------------------------------------------------------

    @Override
    public @NonNull Session save(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");

        // 已存在的会话保留事件日志与 createdAt，其余字段覆盖
        AiSessionEntity entity = toSessionEntity(session, this.aiSessionService.findBySessionId(session.id()));
        this.aiSessionService.save(entity);

        // Re-read：createdAt 由填充器/数据库生成，入参不一定是最终落库的值
        AiSessionEntity saved = this.aiSessionService.findBySessionId(session.id());
        return toSession(saved != null ? saved : entity);
    }

    @Override
    public boolean saveIfAbsent(@NonNull Session session) {
        Assert.notNull(session, "session must not be null");
        if (this.aiSessionService.findBySessionId(session.id()) != null) {
            return false;
        }
        this.aiSessionService.save(toSessionEntity(session, null));
        return true;
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
        return list.stream().map(this::toSession).toList();
    }

    @Override
    @Transactional
    public void delete(@NonNull String sessionId) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        aiSessionEventService.deleteBySessionId(sessionId);
        aiSessionService.deleteBySessionId(sessionId);
    }

    @Override
    @Transactional
    public int deleteExpiredSessions(@NonNull Instant before) {
        Assert.notNull(before, "before must not be null");
        List<AiSessionEntity> expired = this.aiSessionService.findExpiredSessions(LocalDateTime.ofInstant(before, ZoneOffset.UTC));
        for (AiSessionEntity entity : expired) {
            this.aiSessionEventService.deleteBySessionId(entity.getSessionId());
            this.aiSessionService.deleteBySessionId(entity.getSessionId());
        }
        return expired.size();
    }

    // -------------------------------------------------------------------------
    // SessionRepository — event log
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void appendEvent(@NonNull SessionEvent event) {
        Assert.notNull(event, "event must not be null");
        AiSessionEntity sessionEntity = this.getSession(event.getSessionId());
        // 先推进版本再写入事件，两者在同一事务内提交；版本为 0 起步
        long version = sessionEntity.getEventVersion() == null ? 0L : sessionEntity.getEventVersion();
        sessionEntity.setEventVersion(version + 1);
        this.aiSessionService.updateById(sessionEntity);
        this.aiSessionEventService.save(toEventEntity(event, event.getSessionId()));
    }

    @Override
    @Transactional
    public boolean applyCompaction(@NonNull String sessionId, @NonNull CompactionPlan plan, long expectedVersion) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        Assert.notNull(plan, "plan must not be null");

        AiSessionEntity sessionEntity = this.getSession(sessionId);

        // 原子抢占版本槽：版本已被其他写者改动则整单放弃，不触碰事件日志
        if (this.aiSessionService.incrementEventVersionIfMatch(sessionEntity.getId(), expectedVersion) == 0) {
            return false;
        }
        if (plan.isEmpty()) {
            return true;
        }

        // 当前 active 窗口（按 id 升序即逻辑顺序）
        List<AiSessionEventEntity> activeEvents = this.aiSessionEventService.findActiveBySessionId(sessionId);
        Map<String, AiSessionEventEntity> byEventId = activeEvents.stream()
            .collect(Collectors.toMap(e -> String.valueOf(e.getId()), Function.identity(), (a, b) -> a, LinkedHashMap::new));

        // 分组插入：锚点为 null 的追加到末尾，其余插到锚点事件之前
        Map<String, List<SessionEvent>> insertBefore = new LinkedHashMap<>();
        List<SessionEvent> append = new ArrayList<>();
        for (CompactionPlan.Insert insert : plan.inserts()) {
            String anchor = insert.beforeEventId();
            if (anchor == null) {
                append.addAll(insert.events());
                continue;
            }
            if (!byEventId.containsKey(anchor)) {
                throw new IllegalArgumentException("inserts refers to an anchor event that is not in the log of session " + sessionId);
            }
            insertBefore.computeIfAbsent(anchor, id -> new ArrayList<>()).addAll(insert.events());
        }

        Set<String> archiveIds = plan.archiveIds();
        for (String eventId : archiveIds) {
            if (!byEventId.containsKey(eventId)) {
                throw new IllegalArgumentException("archiveIds contains an event that is not in the log of session " + sessionId);
            }
        }

        // 归档的事件原地打标记保留（Recall Storage 仍可检索），其余事件保持相对顺序
        List<Long> archivedEntityIds = archiveIds.stream().map(id -> byEventId.get(id).getId()).toList();
        List<AiSessionEventEntity> newWindow = new ArrayList<>();
        for (AiSessionEventEntity e : activeEvents) {
            String eventId = String.valueOf(e.getId());
            for (SessionEvent event : insertBefore.getOrDefault(eventId, List.of())) {
                newWindow.add(toEventEntity(event, sessionId));
            }
            if (!archiveIds.contains(eventId)) {
                newWindow.add(e);
            }
        }
        for (SessionEvent event : append) {
            newWindow.add(toEventEntity(event, sessionId));
        }

        if (!archivedEntityIds.isEmpty()) {
            this.aiSessionEventService.archiveByIds(archivedEntityIds);
        }
        this.aiSessionEventService.replaceActiveWindow(sessionId, newWindow);
        return true;
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

        // 正则与多关键词无法下推成可移植 SQL，窗口改为内存中处理
        boolean inMemoryWindow = filter.pattern() != null || (filter.keywords() != null && !filter.keywords().isEmpty());
        List<AiSessionEventEntity> list = this.aiSessionEventService.findEvents(sessionId, inMemoryWindow ? filter.withoutWindow() : filter);
        List<SessionEvent> events = list.stream().map(this::toSessionEvent).toList();

        if (inMemoryWindow) {
            return filter.apply(events);
        }

        // lastN 由 SQL 取倒序前 N，翻回正序后按 turn 边界向前补齐
        if (filter.lastN() != null) {
            List<SessionEvent> result = new ArrayList<>(events);
            Collections.reverse(result);
            return Collections.unmodifiableList(extendToTurnStart(sessionId, filter, result));
        }
        return Collections.unmodifiableList(events);
    }

    /**
     * lastN 窗口落在某个 turn 中间时，向前补齐到该 turn 的起点（USER 事件），
     * 避免只返回工具调用而没有它所属的用户消息。仅在可能生效时多查一次：
     * 窗口取满、首个事件不是 turn 起点、且过滤条件不是文本检索、也没有排除 USER 事件。
     */
    private List<SessionEvent> extendToTurnStart(String sessionId, EventFilter filter, List<SessionEvent> window) {
        Integer lastN = filter.lastN();
        if (lastN == null || window.isEmpty() || window.size() < lastN || window.get(0).isTurnStart()
            || filter.hasTextCriteria()
            || (filter.messageTypes() != null && !filter.messageTypes().contains(MessageType.USER))) {
            return window;
        }
        List<AiSessionEventEntity> matched = this.aiSessionEventService.findEvents(sessionId, filter.withoutWindow());
        List<SessionEvent> all = matched.stream().map(this::toSessionEvent).toList();
        return filter.applyTurnAwareWindow(all);
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

    private Long resolveTenantId(Session session) {
        return MapUtils.getLong(session.metadata(), CAHT_CONTEXT_TENANT_ID_KEY, TenantContext.getTenantId());
    }

    /**
     * 把 {@link Session} 的元数据映射到会话实体。传入已有实体时只覆盖可变字段，
     * 保留 createdAt 与事件日志；传入 {@code null} 时按新建处理。
     */
    private AiSessionEntity toSessionEntity(Session session, @Nullable AiSessionEntity existing) {
        AiSessionEntity entity = (existing != null) ? existing : new AiSessionEntity();
        if (existing == null) {
            entity.setCreatedAt(LocalDateTime.ofInstant(session.createdAt(), ZoneOffset.UTC));
            entity.setEventVersion(0L);
            entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        }
        entity.setTenantId(resolveTenantId(session));
        entity.setSessionId(session.id());
        entity.setUserId(session.userId());
        entity.setMetadata(toJson(session.metadata()));
        entity.setExpiresAt(toLocalDateTime(session.expiresAt()));
        return entity;
    }

    private Session toSession(AiSessionEntity e) {
        Session.Builder builder = Session.builder()
            .id(String.valueOf(e.getSessionId()))
            .userId(e.getUserId() != null ? e.getUserId() : "")
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
        return SessionEvent.builder()
            .id(String.valueOf(e.getId()))
            .sessionId(String.valueOf(e.getSessionId()))
            .timestamp(e.getTimestamp() != null ? e.getTimestamp().toInstant(ZoneOffset.UTC) : Instant.now())
            .message(message)
            .archived(e.getArchived() != null && e.getArchived() == 1)
            .metadata(metadata)
            .build();
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
        entity.setArchived(event.isArchived() ? ActiveTypeEnum.ENABLED.getValue() : ActiveTypeEnum.DISABLED.getValue());
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

    @Nullable
    private LocalDateTime toLocalDateTime(@Nullable Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

}
