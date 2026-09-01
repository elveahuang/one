package cc.wdev.platform.commons.autoconfigure.message;

import cc.wdev.platform.commons.autoconfigure.message.properties.WebSocketProperties;
import cc.wdev.platform.commons.message.socket.servlet.interceptor.WebSocketSessionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({WebSocketProperties.class})
@ConditionalOnClass(WebSocketSession.class)
@ConditionalOnProperty(prefix = WebSocketProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketAutoConfiguration {

    public WebSocketAutoConfiguration() {
        log.info("WebSocketAutoConfiguration is enabled.");
    }

    @EnableWebSocket
    @AutoConfiguration
    @Configuration(proxyBeanMethods = false)
    public static class ServletWebSocketServerConfiguration {

        public ServletWebSocketServerConfiguration() {
            log.info("ServletWebSocketServerConfiguration is enabled.");
        }

        /**
         * 会话握手拦截器
         */
        @Bean
        @ConditionalOnMissingBean
        public WebSocketSessionInterceptor webSocketSessionInterceptor() {
            return new WebSocketSessionInterceptor();
        }

    }

    @Configuration(proxyBeanMethods = false)
    public static class NettyWebSocketServerConfiguration {

        public NettyWebSocketServerConfiguration() {
            log.info("NettyWebSocketServerConfiguration is enabled.");
        }

    }

}
