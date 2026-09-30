package cc.wdev.platform.commons.message.topic;

import cc.wdev.platform.commons.core.cache.utils.RedissonUtils;
import cc.wdev.platform.commons.message.rabbit.RabbitUtils;

/**
 * @author elvea
 */
public interface TopicManager {

    Long getNodeId();

    BroadcastType getBroadcastType();

    RedissonUtils getRedissonUtils();

    RabbitUtils getRabbitUtils();

}
