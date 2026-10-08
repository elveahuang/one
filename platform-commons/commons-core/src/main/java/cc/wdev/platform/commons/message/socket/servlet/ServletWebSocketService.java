package cc.wdev.platform.commons.message.socket.servlet;

import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.topic.AbstractTopicService;
import cc.wdev.platform.commons.message.topic.TopicConfig;
import cc.wdev.platform.commons.message.topic.TopicManager;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author elvea
 */
public class ServletWebSocketService extends AbstractTopicService<SimpleMessage<?>, WebSocketSession> {

    public ServletWebSocketService(TopicConfig config, TopicManager topicManager, ServletWebSocketManager servletWebSocketManager) {
        super(config, topicManager, servletWebSocketManager);
    }

}
