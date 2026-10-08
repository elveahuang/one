package cc.wdev.platform.commons.message.rabbit;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Collections;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class RabbitUtils {

    @Getter
    private RabbitAdmin rabbitAdmin;

    @Getter
    private RabbitTemplate rabbitTemplate;

    @Getter
    private SimpleRabbitListenerContainerFactory containerFactory;

    public MessageConverter getMessageConverter() {
        return this.rabbitTemplate.getMessageConverter();
    }

    public <T> void send(String routingKey, T body) throws Exception {
        this.rabbitTemplate.convertAndSend(routingKey, body);
    }

    public <T> void send(String exchange, String routingKey, T body) throws Exception {
        this.rabbitTemplate.convertAndSend(exchange, routingKey, body);
    }

    /**
     * 注册广播队列并注册监听器
     */
    public void addListener(Long nodeId, String exchange, String dlx, ChannelAwareMessageListener listener) {
        // 每个节点独立队列名
        String queueName = exchange + "." + nodeId;

        // 声明交换机
        rabbitAdmin.declareExchange(new FanoutExchange(exchange, true, false));

        // 声明死信队列
        String dlxName = queueName + ".dlq";
        rabbitAdmin.declareQueue(new Queue(dlxName, true, false, false, Collections.emptyMap()));

        // 声明主队列绑定私信队列
        Map<String, Object> args = Maps.newHashMap();
        args.put("x-dead-letter-exchange", dlx);
        args.put("x-dead-letter-routing-key", dlxName);
        rabbitAdmin.declareQueue(new Queue(queueName, true, false, false, args));
        rabbitAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchange, "", args));

        // 声明交换机
        rabbitAdmin.declareExchange(new DirectExchange(dlx, true, false));
        rabbitAdmin.declareBinding(new Binding(dlxName, Binding.DestinationType.QUEUE, dlx, dlxName, Collections.emptyMap()));

        // 注册监听器
        this.registerListener(queueName, listener);
    }

    /**
     * 注册监听器
     */
    public void registerListener(String queueName, ChannelAwareMessageListener listener) {
        // 设置监听器
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setQueueNames(queueName);
        endpoint.setMessageListener(listener);
        // 注册容器并启动容器
        MessageListenerContainer container = this.containerFactory.createListenerContainer(endpoint);
        container.start();
    }

}
