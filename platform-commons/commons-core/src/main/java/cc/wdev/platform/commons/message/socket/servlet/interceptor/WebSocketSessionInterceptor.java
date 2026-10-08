package cc.wdev.platform.commons.message.socket.servlet.interceptor;

import cc.wdev.platform.commons.message.session.UserSession;
import cc.wdev.platform.commons.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static cc.wdev.platform.commons.message.socket.WebSocketConstants.SOCKET_USER_SESSION_KEY;

/**
 * @author elvea
 */
@Slf4j
public class WebSocketSessionInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        try {
            if (SecurityUtils.isAuthenticated()) {
                String uType = ((ServletServerHttpRequest) request).getServletRequest().getParameter("uType");
                UserSession wsUserSession = new UserSession();
                wsUserSession.setUid(SecurityUtils.getUid());
                wsUserSession.setUid(SecurityUtils.getUid());
                wsUserSession.setUsername(SecurityUtils.getUsername());
                wsUserSession.setTid(SecurityUtils.getTid());
                wsUserSession.setUType(uType);
                wsUserSession.setLast(System.currentTimeMillis());
                attributes.put(SOCKET_USER_SESSION_KEY, wsUserSession);
                return true;
            }
        } catch (Exception e) {
            log.error("beforeHandshake error", e);
        }
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        //
    }

}
