package cc.wdev.platform.system.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cc.wdev.platform.system.commons.constants.SystemRabbitConstants.*;

/**
 * @author elvea
 */
@Slf4j
@Configuration
public class SystemRabbitConfiguration {

    @Bean
    public Queue loginSessionQueue() {
        return new Queue(LOGIN_SESSION);
    }

    @Bean
    public Queue applicationLogQueue() {
        return new Queue(APPLICATION_LOG_QUEUE);
    }

    @Bean
    public Queue captchaLogQueue() {
        return new Queue(CAPTCHA_LOG_QUEUE);
    }

    @Bean
    public Queue operationLogQueue() {
        return new Queue(OPERATION_LOG_QUEUE);
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(MESSAGE_QUEUE);
    }

    // --------------------------------------------------------------------------------------------------------------------------------
    // 公共死信：消费失败的消息在应用层转入死信交换机留存，便于人工排查（不改动既有队列参数）
    // --------------------------------------------------------------------------------------------------------------------------------

    @Bean
    public FanoutExchange commonDeadLetterExchange() {
        return ExchangeBuilder.fanoutExchange(COMMON_DEAD_LETTER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue commonDeadLetterQueue() {
        return QueueBuilder.durable(COMMON_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding commonDeadLetterBinding() {
        return BindingBuilder.bind(commonDeadLetterQueue()).to(commonDeadLetterExchange());
    }

    // --------------------------------------------------------------------------------------------------------------------------------
    // 会员订单超时处理
    // --------------------------------------------------------------------------------------------------------------------------------

    // 死信队列
    @Bean(name = "vipOrderQueue")
    public Queue vipOrderQueue() {
        return QueueBuilder.durable(VIP_ORDER_QUEUE).build();
    }

    // 死信交换机
    @Bean(name = "vipOrderExchange")
    public Exchange vipOrderExchange() {
        return ExchangeBuilder.directExchange(VIP_ORDER_EXCHANGE).durable(true).build();
    }

    // 死信队列与死信交换机绑定
    @Bean(name = "vipOrderBinding")
    public Binding vipOrderBinding() {
        return BindingBuilder
            .bind(vipOrderQueue())
            .to(vipOrderExchange())
            .with(VIP_ORDER_ROUTING_KEY)
            .noargs();
    }

    // 普通队列direct交换机
    @Bean(name = "vipOrderTtlExchange")
    public Exchange vipOrderTtlExchange() {
        return ExchangeBuilder.directExchange(VIP_ORDER_TTL_EXCHANGE).durable(true).build();
    }

    // 将普通队列绑定到死信队列交换机上
    @Bean(name = "vipOrderTtlQueue")
    public Queue vipOrderTtlQueue() {
        return QueueBuilder.durable(VIP_ORDER_TTL_QUEUE)
            .deadLetterExchange(VIP_ORDER_EXCHANGE)
            .deadLetterRoutingKey(VIP_ORDER_ROUTING_KEY)
            .withArgument(ARG_MESSAGE_TTL, 1000 * 60 * 60 * 24) // 24小时后处理死信
            .build();
    }

    // 普通队列绑定direct交换机
    @Bean(name = "vipOrderTtlBinding")
    public Binding vipOrderTtlBinding() {
        return BindingBuilder
            .bind(vipOrderTtlQueue())
            .to(vipOrderTtlExchange())
            .with(VIP_ORDER_TTL_ROUTING_KEY)
            .noargs();
    }

    // --------------------------------------------------------------------------------------------------------------------------------
    // 用于控制是否允许多处登录
    // --------------------------------------------------------------------------------------------------------------------------------

    @Bean
    public FanoutExchange multipleLoginQueueExchange() {
        return new FanoutExchange(MULTIPLE_LOGIN_QUEUE_EXCHANGE);
    }

    @Bean
    public Queue multipleLoginQueue() {
        return new Queue(MULTIPLE_LOGIN_QUEUE);
    }

    @Bean
    public Binding multipleLoginQueueBinding() {
        return BindingBuilder.bind(multipleLoginQueue()).to(multipleLoginQueueExchange());
    }

}
