package cc.wdev.platform.commons.message.socket.servlet.handler;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.message.session.UserSession;
import cc.wdev.platform.commons.message.socket.servlet.ServletWebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author elvea
 */
@Slf4j
public abstract class AbstractWebSocketHandler extends TextWebSocketHandler {

    protected abstract ServletWebSocketManager getWebSocketManager();

    /**
     * @see TextWebSocketHandler#afterConnectionEstablished(WebSocketSession)
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("Connection Established sessionId: {}", session.getId());
        this.getWebSocketManager().createSession(session);
        UserSession userSession = this.getWebSocketManager().getUserSession(session);
        TenantContext.setTenantId(userSession.getTid());
    }

    /**
     * @see TextWebSocketHandler#afterConnectionClosed(WebSocketSession, CloseStatus)
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,
                                      @NonNull CloseStatus status) {
        log.info("Connection Closed sessionId: {}", session.getId());
        this.getWebSocketManager().closeSession(session);
        TenantContext.clear();
    }

    /**
     * @see TextWebSocketHandler#handleTransportError(WebSocketSession, Throwable)
     */
    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable e) {
        log.error("Transport error sessionId: {}, exception: {}", session.getId(), e.getMessage(), e);
    }

    /**
     * @see TextWebSocketHandler#handlePongMessage(WebSocketSession, PongMessage)
     */
    @Override
    protected void handlePongMessage(@NonNull WebSocketSession session, @NonNull PongMessage message) {
        this.getWebSocketManager().updateSession(session);
    }

}
