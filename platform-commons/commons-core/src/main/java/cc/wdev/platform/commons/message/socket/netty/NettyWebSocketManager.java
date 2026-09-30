package cc.wdev.platform.commons.message.socket.netty;

import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.session.UserSession;
import cc.wdev.platform.commons.message.socket.WebSocketManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cc.wdev.platform.commons.message.socket.WebSocketConstants.SOCKET_USER_SESSION_KEY;

/**
 * @author elvea
 */
@Slf4j
public class NettyWebSocketManager implements WebSocketManager<String, Channel> {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final Map<String, ChannelId> channelIdMap = new ConcurrentHashMap<>();

    private final Map<String, Map<String, UserSession>> userSessionMap = new ConcurrentHashMap<>();

    /**
     * @see WebSocketManager#createSession(Object)
     */
    @Override
    public void createSession(Channel channel) {
        String channelId = channel.id().toString();

        // 保存会话
        this.channelGroup.add(channel);
        this.channelIdMap.putIfAbsent(channelId, channel.id());

        // 保存用户会话
        UserSession userSession = this.getUserSession(channel);
        String key = String.valueOf(userSession.getUid());
        userSessionMap.putIfAbsent(key, new ConcurrentHashMap<>());
        userSessionMap.get(key).put(channelId, userSession);
    }

    /**
     * @see WebSocketManager#updateSession(Object)
     */
    @Override
    public void updateSession(Channel channel) {
        String channelId = channel.id().toString();

        // 保存用户会话
        UserSession userSession = this.getUserSession(channel);
        String key = String.valueOf(userSession.getUid());
        userSessionMap.putIfAbsent(key, new ConcurrentHashMap<>());
        userSessionMap.get(key).put(channelId, userSession);
    }

    /**
     * @see WebSocketManager#closeSession(Object)
     */
    @Override
    public void closeSession(Channel channel) {
        String channelId = channel.id().toString();

        // 移除会话
        this.channelGroup.remove(channel);
        this.channelIdMap.remove(channelId);

        // 移除用户会话
        UserSession userSession = this.getUserSession(channel);

        String key = String.valueOf(userSession.getUid());
        Map<String, UserSession> sessions = this.userSessionMap.get(key);
        if (MapUtils.isNotEmpty(sessions)) {
            boolean result = sessions.remove(channelId) != null;
            log.info("Close Web Socket Session [{}] [{}] - {}.", key, channel.id(), result);
            if (sessions.isEmpty()) {
                this.userSessionMap.remove(key);
                log.info("Close all Web Socket Session [{}].", key);
            }
        }
    }

    /**
     * @see WebSocketManager#exist(Serializable)
     */
    @Override
    public boolean exist(String sessionId) {
        return this.channelIdMap.containsKey(sessionId);
    }

    /**
     * @see WebSocketManager#getSession(Serializable)
     */
    @Override
    public Channel getSession(String sessionId) {
        return this.channelGroup.find(this.channelIdMap.get(sessionId));
    }

    /**
     * @see WebSocketManager#getSessions()
     */
    @Override
    public List<Channel> getSessions() {
        return this.channelGroup.stream().toList();
    }

    /**
     * @see WebSocketManager#getSession(Serializable)
     */
    @Override
    public List<Channel> getUserSessions(String key) {
        return this.getUserSessionMap(key).keySet().stream().map(this.channelIdMap::get).map(this.channelGroup::find).toList();
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
    public UserSession getUserSession(Channel channel) {
        return (UserSession) channel.attr(AttributeKey.valueOf(SOCKET_USER_SESSION_KEY)).get();
    }

    /**
     * @see WebSocketManager#checkUserSession(Object)
     */
    @Override
    public boolean checkUserSession(Channel channel) {
        UserSession userSession = this.getUserSession(channel);
        if (userSession == null) {
            // 推送登录提示
            this.sendTextMessage(channel, "Login Required");
            // 关闭连接
            this.closeSession(channel);
            return false;
        }
        return true;
    }

    /**
     * @see WebSocketManager#sendTextMessage(Object, String)
     */
    @Override
    public void sendTextMessage(Channel channel, String text) {
        channel.writeAndFlush(new TextWebSocketFrame(text));
    }

    /**
     * @see WebSocketManager#sendMessage(SimpleMessage)
     */
    @Override
    public <M extends SimpleMessage<?>> void sendMessage(M message) {
    }

    /**
     * @see WebSocketManager#sendMessage(Object, SimpleMessage)
     */
    @Override
    public <M extends SimpleMessage<?>> void sendMessage(Channel session, M message) {
    }

}
