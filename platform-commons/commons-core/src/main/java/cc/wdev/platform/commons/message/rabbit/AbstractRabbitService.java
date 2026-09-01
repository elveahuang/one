package cc.wdev.platform.commons.message.rabbit;

import cc.wdev.platform.commons.core.GlobalContext;
import cc.wdev.platform.commons.service.AbstractService;
import cc.wdev.platform.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public abstract class AbstractRabbitService<T> extends AbstractService implements RabbitService<T> {

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

    @RabbitHandler
    public void handler(T t) {
        try {
            execute(t);
        } catch (Exception e) {
            log.error("RabbitMQ error.", e);
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
