package cc.wdev.platform.commons.message.topic;

import cc.wdev.platform.commons.core.cache.utils.RedissonUtils;
import cc.wdev.platform.commons.message.rabbit.RabbitUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class DefaultTopicManager implements TopicManager {

    private final Config config;

    private final RedissonUtils redissonUtils;

    private final RabbitUtils rabbitUtils;

    @Override
    public Long getNodeId() {
        return this.config.getNodeId();
    }

    @Override
    public BroadcastType getBroadcastType() {
        return this.config.getType();
    }

    @Override
    public RedissonUtils getRedissonUtils() {
        return this.redissonUtils;
    }

    @Override
    public RabbitUtils getRabbitUtils() {
        return this.rabbitUtils;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config implements Serializable {
        private Long nodeId;
        private BroadcastType type;
    }

}
