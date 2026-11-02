package cc.wdev.platform.commons.message.rabbit;

import cc.wdev.platform.commons.core.GlobalContext;
import cc.wdev.platform.commons.service.AbstractService;
import cc.wdev.platform.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public abstract class AbstractRabbitService<T> extends AbstractService implements RabbitService<T> {

    /**
     * 公共死信交换机（fanout，由各应用声明并绑定死信队列）；与 system 侧 SystemRabbitConstants 保持一致
     */
    public static final String COMMON_DEAD_LETTER_EXCHANGE = "COMMON_DEAD_LETTER_EXCHANGE";

    protected GlobalContext context;

    protected RabbitTemplate rabbitTemplate;

    /**
     * @see RabbitService#send(Object)
     */
    @Override
    public void send(T body) throws Exception {
        log.info("Send Amqp Message - {}", JacksonUtils.toJson(body));
        this.send(getRoutingKey(), body);
    }

    /**
     * @see RabbitService#send(String, Object)
     */
    @Override
    public void send(String routingKey, T body) throws Exception {
        if (this.isEnabled()) {
            log.info("RabbitMQ is enabled. send [{}]...", routingKey);
            this.rabbitTemplate.convertAndSend(routingKey, body);
        } else {
            this.execute(body);
        }
    }

    /**
     * @see RabbitService#send(String, Object)
     */
    @Override
    public void send(String exchange, String routingKey, T body) throws Exception {
        if (this.isEnabled()) {
            log.info("RabbitMQ is enabled. send [{}][{}]...", exchange, routingKey);
            this.rabbitTemplate.convertAndSend(exchange, routingKey, body);
        } else {
            this.execute(body);
        }
    }

    /**
     * 事务内安全发送：存在活动事务时延迟到提交后发送，避免消费者读到未提交数据；
     * 无事务时与 {@link #send(String, Object)} 行为一致。
     */
    @Override
    public void sendAfterCommit(String routingKey, T body) throws Exception {
        if (this.isEnabled() && TransactionSynchronizationManager.isSynchronizationActive()) {
            log.info("RabbitMQ send deferred to after-commit [{}]...", routingKey);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        rabbitTemplate.convertAndSend(routingKey, body);
                    } catch (Exception e) {
                        log.error("RabbitMQ after-commit send failed. routingKey - [{}].", routingKey, e);
                    }
                }
            });
            return;
        }
        this.send(routingKey, body);
    }

    @RabbitHandler
    public void handler(T t) {
        try {
            execute(t);
        } catch (Exception e) {
            log.error("RabbitMQ error.", e);
            this.sendToDeadLetter(t, e);
        }
    }

    /**
     * 消费失败的消息转入公共死信交换机留存；死信投递自身的异常只记录不再抛出，避免消息循环
     */
    protected void sendToDeadLetter(T t, Exception cause) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("queue", getRoutingKey());
            payload.put("message", JacksonUtils.toJson(t));
            payload.put("error", String.valueOf(cause));
            payload.put("timestamp", LocalDateTime.now().toString());
            this.rabbitTemplate.convertAndSend(COMMON_DEAD_LETTER_EXCHANGE, "", payload);
        } catch (Exception e) {
            log.error("RabbitMQ dead-letter publish failed. queue - [{}].", getRoutingKey(), e);
        }
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    public void setContext(GlobalContext context) {
        this.context = context;
    }

    protected boolean isEnabled() {
        return context.getRabbit().isEnabled();
    }

    public abstract void execute(T t) throws Exception;

    public abstract String getRoutingKey();

}
