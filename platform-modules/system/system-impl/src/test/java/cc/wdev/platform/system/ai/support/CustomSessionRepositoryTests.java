package cc.wdev.platform.system.ai.support;

import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionIndexEntity;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionIndexService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.session.EventType;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionAttributes;
import org.springframework.ai.session.SessionEvent;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static cc.wdev.platform.commons.ai.AiConstants.CHAT_CONTEXT_TENANT_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author elvea
 */
class CustomSessionRepositoryTests {

    private AiSessionService aiSessionService;

    private AiSessionEventService aiSessionEventService;

    private AiSessionIndexService aiSessionIndexService;

    private CustomSessionRepository repository;

    @BeforeEach
    void setUp() {
        this.aiSessionService = mock(AiSessionService.class);
        this.aiSessionEventService = mock(AiSessionEventService.class);
        this.aiSessionIndexService = mock(AiSessionIndexService.class);
        this.repository = new CustomSessionRepository(
            this.aiSessionService,
            this.aiSessionEventService,
            this.aiSessionIndexService,
            mock(PlatformTransactionManager.class));
    }

    @Test
    void saveMapsPrincipalNameAndTenantMetadata() {
        AtomicReference<AiSessionEntity> stored = new AtomicReference<>();
        when(this.aiSessionService.findBySessionId("session-1")).thenAnswer(_ -> stored.get());
        when(this.aiSessionService.save(any(AiSessionEntity.class))).thenAnswer(invocation -> {
            AiSessionEntity entity = invocation.getArgument(0);
            stored.set(entity);
            return entity;
        });

        Session session = Session.builder()
            .id("session-1")
            .createdAt(Instant.parse("2026-10-26T00:00:00Z"))
            .metadata(Map.of(
                SessionAttributes.PRINCIPAL_NAME, "42",
                CHAT_CONTEXT_TENANT_ID, 7L))
            .build();

        Session saved = this.repository.save(session);

        assertEquals("42", saved.metadata().get(SessionAttributes.PRINCIPAL_NAME));
        assertEquals("42", stored.get().getUserId());
        assertEquals(7L, stored.get().getTenantId());
        assertEquals(0L, stored.get().getEventVersion());
        verify(this.aiSessionIndexService).replaceSessionIndexes(
            eq("session-1"), eq(7L), eq(session.metadata()), anySet());
    }

    @Test
    void findByMetadataUsesIndexAndVerifiesFullMetadata() {
        AiSessionIndexEntity index = AiSessionIndexEntity.builder()
            .sessionId("session-1")
            .attrName(SessionAttributes.PRINCIPAL_NAME)
            .attrValue("42")
            .build();
        AiSessionEntity entity = new AiSessionEntity();
        entity.setSessionId("session-1");
        entity.setActive(1);
        entity.setCreatedAt(java.time.LocalDateTime.parse("2026-10-26T00:00:00"));
        entity.setMetadata("{\"principalName\":\"42\"}");
        when(this.aiSessionIndexService.findByAttrNameAndValue(SessionAttributes.PRINCIPAL_NAME, "42"))
            .thenReturn(List.of(index));
        when(this.aiSessionService.findBySessionIds(anyCollection())).thenReturn(List.of(entity));

        List<Session> sessions = this.repository.findByMetadata(SessionAttributes.PRINCIPAL_NAME, "42");

        assertEquals(1, sessions.size());
        assertEquals("session-1", sessions.getFirst().id());
    }

    @Test
    void appendEventPersistsMessageTypeAndPayload() {
        AiSessionEntity session = new AiSessionEntity();
        session.setTenantId(7L);
        when(this.aiSessionService.incrementEventVersion("session-1")).thenReturn(1);
        when(this.aiSessionService.findBySessionId("session-1")).thenReturn(session);
        when(this.aiSessionEventService.findBySessionEventId("event-1")).thenReturn(null);

        SessionEvent event = SessionEvent.builder()
            .id("event-1")
            .sessionId("session-1")
            .timestamp(Instant.parse("2026-10-26T00:00:00Z"))
            .message(UserMessage.builder().text("hello").build())
            .build();

        this.repository.appendEvent(event);

        ArgumentCaptor<AiSessionEventEntity> captor = ArgumentCaptor.forClass(AiSessionEventEntity.class);
        verify(this.aiSessionEventService).save(captor.capture());
        assertEquals(EventType.MESSAGE.name(), captor.getValue().getEventType());
        assertEquals("USER", captor.getValue().getMessageType());
        assertEquals("hello", captor.getValue().getMessageContent());
        assertTrue(captor.getValue().getPayload().contains("hello"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void appendEventsPersistsCompactionBoundaryWithoutMessageType() {
        AiSessionEntity session = new AiSessionEntity();
        session.setTenantId(7L);
        when(this.aiSessionService.casIncrementEventVersion("session-1", 3L)).thenReturn(1);
        when(this.aiSessionService.findBySessionId("session-1")).thenReturn(session);
        when(this.aiSessionEventService.findBySessionEventIds(anyList())).thenReturn(List.of());

        SessionEvent boundary = SessionEvent.builder()
            .id("boundary-1")
            .sessionId("session-1")
            .type(EventType.COMPACTION)
            .payload(Map.of("summary", List.of("summary-1")))
            .build();

        assertTrue(this.repository.appendEvents("session-1", List.of(boundary), 3L));

        ArgumentCaptor<List<AiSessionEventEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(this.aiSessionEventService).saveBatch(captor.capture());
        AiSessionEventEntity entity = captor.getValue().getFirst();
        assertEquals(EventType.COMPACTION.name(), entity.getEventType());
        assertNull(entity.getMessageType());
        assertTrue(entity.getPayload().contains("summary-1"));
    }

    @Test
    void deleteRemovesSessionEventAndIndexWithoutForeignKeyCascade() {
        this.repository.delete("session-1");

        InOrder order = inOrder(this.aiSessionService, this.aiSessionEventService, this.aiSessionIndexService);
        order.verify(this.aiSessionService).deleteBySessionId("session-1");
        order.verify(this.aiSessionEventService).deleteBySessionId("session-1");
        order.verify(this.aiSessionIndexService).deleteBySessionId("session-1");
    }

    @Test
    void deleteExpiredSessionsRemovesChildrenOnlyWhenSessionWasDeleted() {
        AiSessionEntity session = new AiSessionEntity();
        session.setSessionId("session-1");
        when(this.aiSessionService.findExpiredSessions(any())).thenReturn(List.of(session));
        when(this.aiSessionService.deleteExpiredSession(eq("session-1"), any())).thenReturn(1);

        assertEquals(1, this.repository.deleteExpiredSessions(Instant.parse("2026-10-26T00:00:00Z")));

        verify(this.aiSessionEventService).deleteBySessionId("session-1");
        verify(this.aiSessionIndexService).deleteBySessionId("session-1");
    }

}
