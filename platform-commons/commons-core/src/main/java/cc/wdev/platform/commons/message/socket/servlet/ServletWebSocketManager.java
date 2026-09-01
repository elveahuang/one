package cc.wdev.platform.commons.message.socket.servlet;

import cc.wdev.platform.commons.message.model.SimpleJsonMessage;
import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.model.SimpleTextMessage;
import cc.wdev.platform.commons.message.session.UserSession;
import cc.wdev.platform.commons.message.socket.WebSocketManager;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cc.wdev.platform.commons.message.socket.WebSocketConstants.SOCKET_USER_SESSION_KEY;

/**
 * @author elvea
 */
@Slf4j
public class ServletWebSocketManager implements WebSocketManager<String, WebSocketSession> {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private final Map<String, Map<String, UserSession>> userSessionMap = new ConcurrentHashMap<>();

    /**
     * @see WebSocketManager#createSession(Object)
     */
    @Override
    public void createSession(WebSocketSession session) {
        String sessionId = session.getId();

        // 保存会话
        this.sessionMap.put(sessionId, session);

        // 保存用户会话
        UserSession userSession = this.getUserSession(session);
        userSession.setCid(session.getId());

        String key = String.valueOf(userSession.getUid());
        userSessionMap.putIfAbsent(key, new ConcurrentHashMap<>());
        userSessionMap.get(key).put(sessionId, userSession);
    }

    /**
     * @see WebSocketManager#updateSession(Object)
     */
    @Override
    public void updateSession(WebSocketSession session) {
        String sessionId = session.getId();

        // 保存用户会话
        UserSession userSession = this.getUserSession(session);

        String key = String.valueOf(userSession.getUid());
        userSessionMap.putIfAbsent(key, new ConcurrentHashMap<>());
        userSessionMap.get(key).put(sessionId, userSession);
    }

    /**
     * @see WebSocketManager#closeSession(Object)
     */
    @Override
    public void closeSession(WebSocketSession session) {
        String sessionId = session.getId();

        // 移除会话
        this.sessionMap.remove(sessionId);

        // 移除用户会话
        UserSession userSession = this.getUserSession(session);

        String key = String.valueOf(userSession.getUid());
        Map<String, UserSession> sessions = this.userSessionMap.get(key);
        if (MapUtils.isNotEmpty(sessions)) {
            boolean result = sessions.remove(sessionId) != null;
            log.info("Close Web Socket User Session [{}] [{}] - {}", key, sessionId, result);
            if (sessions.isEmpty()) {
                this.userSessionMap.remove(key);
                log.info("Close all Web Socket User Session [{}]", key);
            }
        }
    }

    /**
     * @see WebSocketManager#exist(Serializable)
     */
    @Override
    public boolean exist(String sessionId) {
        return this.sessionMap.containsKey(sessionId);
    }

    /**
     * @see WebSocketManager#getSession(Serializable)
     */
    @Override
    public WebSocketSession getSession(String sessionId) {
        return this.sessionMap.get(sessionId);
    }

    /**
     * @see WebSocketManager#getSessions()
     */
    @Override
    public List<WebSocketSession> getSessions() {
        return new ArrayList<>(this.sessionMap.values());
    }

    /**
     * @see WebSocketManager#getUserSessions(String)
     */
    @Override
    public List<WebSocketSession> getUserSessions(String key) {
        return this.getUserSessionMap(key).keySet().stream().map(this.sessionMap::get).toList();
    }

    /**
     * @see WebSocketManager#getUserSessionMap(String)
     */
    @Override
    public Map<String, UserSession> getUserSessionMap(String key) {
        return this.userSessionMap.getOrDefault(key, new ConcurrentHashMap<>());
    }

    /**
     * @see WebSocketManager#getUserSession(Object)
     */
    @Override
    public UserSession getUserSession(WebSocketSession session) {
        return (UserSession) session.getAttributes().get(SOCKET_USER_SESSION_KEY);
    }

    /**
     * @see WebSocketManager#checkUserSession(Object)
     */
    @Override
    public boolean checkUserSession(WebSocketSession session) {
        UserSession userSession = (UserSession) session.getAttributes().get(SOCKET_USER_SESSION_KEY);
        if (userSession == null) {
            // 推送登录提示
            this.sendTextMessage(session, "Login Required");
            // 关闭连接
            this.closeSession(session);
            return false;
        }
        return true;
    }

    /**
     * @see WebSocketManager#sendTextMessage(Object, String)
     */
    @Override
    public void sendTextMessage(WebSocketSession session, String text) {
        if (session == null || !session.isOpen()) {
            log.info("sendMessage error, session is null or closed");
        } else {
            try {
                session.sendMessage(new TextMessage(text));
            } catch (IOException e) {
                log.error("sendMessage error, session [{}] message [{}]", session, text, e);
            }
        }
    }

    /**
     * @see WebSocketManager#sendMessage(SimpleMessage)
     */
    @Override
    public <M extends SimpleMessage<?>> void sendMessage(M message) {
        if (message == null) {
            log.info("sendMessage error, message is null");
            return;
        }

        if (CollectionUtils.isNotEmpty(message.getKeys())) {
            message.getKeys().forEach(key -> {
                if (this.exist(key)) {
                    this.sendTextMessage(this.getSession(key), getMessageContent(message));
                }
            });
        } else if (CollectionUtils.isNotEmpty(message.getReceivers())) {
            message.getReceivers().forEach(receiver -> {
                String key = String.valueOf(receiver);
                this.getUserSessions(key).forEach(s -> this.sendTextMessage(s, getMessageContent(message)));
            });
        } else {
            this.getSessions().forEach(s -> this.sendTextMessage(s, getMessageContent(message)));
        }
    }

    /**
     * @see WebSocketManager#sendMessage(Object, SimpleMessage)
     */
    @Override
    public <M extends SimpleMessage<?>> void sendMessage(WebSocketSession session, M message) {
        this.sendTextMessage(session, getMessageContent(message));
    }

    private <M extends SimpleMessage<?>> String getMessageContent(M message) {
        String content = "";
        if (message.getContent() instanceof SimpleTextMessage || "text".equalsIgnoreCase(message.getType())) {
            content = message.getContent().toString();
        } else if (message.getContent() instanceof SimpleJsonMessage<?> || "json".equalsIgnoreCase(message.getType())) {
            try {
                content = JacksonUtils.toJson(message.getContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return content;
    }

}
