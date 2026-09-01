package cc.wdev.platform.commons.autoconfigure.message;

import cc.wdev.platform.commons.autoconfigure.message.properties.RabbitProperties;
import cc.wdev.platform.commons.message.rabbit.RabbitUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({RabbitTemplate.class, MessageConverter.class})
@EnableConfigurationProperties({RabbitProperties.class})
@ConditionalOnProperty(prefix = RabbitProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitCustomAutoConfiguration {

    public RabbitCustomAutoConfiguration() {
        log.info("RabbitCustomAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(JacksonUtils.getObjectMapper());
        DefaultJacksonJavaTypeMapper mapper = new DefaultJacksonJavaTypeMapper();
        mapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(mapper);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    @DependsOn("rabbitAdmin")
    @ConditionalOnMissingBean
    public RabbitUtils rabbitUtils(RabbitTemplate rabbitTemplate,
                                   RabbitAdmin rabbitAdmin,
                                   SimpleRabbitListenerContainerFactory containerFactory
    ) {
        return new RabbitUtils(rabbitAdmin, rabbitTemplate, containerFactory);
    }

}
