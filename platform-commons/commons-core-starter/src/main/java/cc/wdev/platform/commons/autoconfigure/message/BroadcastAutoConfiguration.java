package cc.wdev.platform.commons.autoconfigure.message;

import cc.wdev.platform.commons.autoconfigure.message.properties.BroadcastProperties;
import cc.wdev.platform.commons.core.cache.utils.RedissonUtils;
import cc.wdev.platform.commons.message.rabbit.RabbitUtils;
import cc.wdev.platform.commons.message.topic.BroadcastType;
import cc.wdev.platform.commons.message.topic.DefaultTopicManager;
import cc.wdev.platform.commons.message.topic.TopicManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({BroadcastProperties.class})
@ConditionalOnProperty(prefix = BroadcastProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class BroadcastAutoConfiguration {

    public BroadcastAutoConfiguration() {
        log.info("BroadcastAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean
    public TopicManager topicManager(BroadcastProperties properties,
                                     ObjectProvider<RedissonUtils> redissonUtilsObjectProvider,
                                     ObjectProvider<RabbitUtils> rabbitUtilsObjectProvider) {
        log.info("Create TopicManager [{}]", properties.getType());

        RedissonUtils redissonUtils = redissonUtilsObjectProvider.getIfAvailable();
        RabbitUtils rabbitUtils = rabbitUtilsObjectProvider.getIfAvailable();

        if (BroadcastType.Redisson.equals(properties.getType()) && redissonUtils == null) {
            log.info("With BroadcastType [{}]. RedissonClient cannot be null. ", properties.getType());
        } else if (BroadcastType.Rabbit.equals(properties.getType()) && rabbitUtils == null) {
            log.info("With BroadcastType [{}]. RabbitTemplate cannot be null. ", properties.getType());
        }
        DefaultTopicManager.Config config = DefaultTopicManager.Config.builder()
            .nodeId(properties.getNodeId())
            .type(properties.getType())
            .build();
        return new DefaultTopicManager(config, redissonUtils, rabbitUtils);
    }

}
