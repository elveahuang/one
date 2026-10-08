package cc.wdev.platform.commons.message.socket;

import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.session.UserSession;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface WebSocketManager<K extends Serializable, S> {

    /**
     * 注册会话
     */
    void createSession(S session);

    /**
     * 更新会话
     */
    void updateSession(S session);

    /**
     * 注销会话
     */
    void closeSession(S session);

    /**
     * 是否存在会话
     */
    boolean exist(K sessionId);

    /**
     * 获取会话
     */
    S getSession(K sessionId);

    /**
     * 获取所有会话
     */
    List<S> getSessions();

    /**
     * 获取用户会话
     */
    List<S> getUserSessions(String key);

    /**
     * 获取用户会话映射
     */
    Map<K, UserSession> getUserSessionMap(String key);

    /**
     * 获取当前登录用户会话
     */
    UserSession getUserSession(S session);

    /**
     * 检查链接是否已经包含用户会话信息
     */
    boolean checkUserSession(S session);

    /**
     * 发送文本消息
     */
    void sendTextMessage(S session, String text);

    /**
     * 发送消息
     */
    <M extends SimpleMessage<?>> void sendMessage(M message);

    /**
     * 发送消息
     */
    <M extends SimpleMessage<?>> void sendMessage(S session, M message);

}
