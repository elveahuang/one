package cc.wdev.platform.commons.message.topic;

import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.socket.WebSocketManager;
import cc.wdev.platform.commons.utils.GenericsUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author elvea
 */
@Slf4j
public abstract class AbstractTopicService<T extends SimpleMessage<?>, S> implements TopicService<T> {

    @Getter
    private final TopicConfig config;

    @Getter
    private final TopicManager manager;

    @Getter
    private final WebSocketManager<String, S> webSocketManager;

    public AbstractTopicService(TopicConfig config,
                                TopicManager manager,
                                WebSocketManager<String, S> webSocketManager) {
        this.config = config;
        this.manager = manager;
        this.webSocketManager = webSocketManager;
        this.init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Class<T> superGenericType = GenericsUtils.getSuperGenericType(getClass(), AbstractTopicService.class, 0);

        if (BroadcastType.Redisson.equals(this.manager.getBroadcastType())) {
            // 创建Redisson Topic并注册监听器
            log.info("Init topic [{}] with redisson. Message Type [{}]", config.getName(), superGenericType.getSimpleName());
            manager.getRedissonUtils().addListener(config.getName(), superGenericType, (channel, msg) -> {
                log.info("Receive message [{}] from channel [{}]", msg, channel);
                this.handle(msg);
            });
        } else if (BroadcastType.Rabbit.equals(this.manager.getBroadcastType())) {
            // 创建RabbitMQ的广播并注册监听器
            log.info("Init topic {} with rabbitmq. Message Type [{}]", config.getName(), superGenericType.getSimpleName());
            manager.getRabbitUtils().addListener(manager.getNodeId(), config.getExchange(), config.getDlx(), (message, channel) -> {
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                log.info("Receive message [{}] from exchange [{}]", body, config.getExchange());

                Object obj = this.manager.getRabbitUtils().getMessageConverter().fromMessage(message);
                if (obj instanceof SimpleMessage<?> msg) {
                    this.handle((T) msg);
                }
            });
        } else {
            log.info("Unsupported broadcast type [{}].", this.manager.getBroadcastType());
        }
    }

    /**
     * @see TopicService#publish(SimpleMessage)
     */
    public void publish(T message) throws Exception {
        if (BroadcastType.Redisson.equals(this.manager.getBroadcastType())) {
            log.info("Publish redisson message [{}] to redis topic [{}]", message, config.getTopicName());
            this.manager.getRedissonUtils().getTopic(config.getTopicName()).publish(message);
        } else if (BroadcastType.Rabbit.equals(this.manager.getBroadcastType())) {
            log.info("Publish rabbit message [{}] to rabbit topic [{}]", message, config.getTopicName());
            this.manager.getRabbitUtils().send(this.config.getExchange(), this.config.getRoutingKey(), message);
        } else {
            log.info("Publish rabbit message [{}] to topic [{}] failed", message, config.getTopicName());
        }
    }

    /**
     * @see TopicService#handle(SimpleMessage)
     */
    @Override
    public void handle(T message) {
        log.info("Topic [{}] Receive message [{}]", config.getTopicName(), message);
        this.webSocketManager.sendMessage(message);
    }

}
