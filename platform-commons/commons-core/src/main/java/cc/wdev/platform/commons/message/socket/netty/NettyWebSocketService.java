package cc.wdev.platform.commons.message.socket.netty;

import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.topic.AbstractTopicService;
import cc.wdev.platform.commons.message.topic.TopicConfig;
import cc.wdev.platform.commons.message.topic.TopicManager;
import io.netty.channel.Channel;

/**
 * @author elvea
 */
public class NettyWebSocketService extends AbstractTopicService<SimpleMessage<?>, Channel> {

    public NettyWebSocketService(TopicConfig config, TopicManager topicManager, NettyWebSocketManager nettyWebSocketManager) {
        super(config, topicManager, nettyWebSocketManager);
    }

}
