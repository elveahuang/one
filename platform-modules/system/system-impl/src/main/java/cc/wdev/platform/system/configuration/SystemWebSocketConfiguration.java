package cc.wdev.platform.system.configuration;

import cc.wdev.platform.commons.message.socket.servlet.ServletWebSocketManager;
import cc.wdev.platform.commons.message.topic.TopicManager;
import cc.wdev.platform.system.core.socket.SystemWebSocketHandler;
import cc.wdev.platform.system.core.socket.SystemWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author elvea
 */
@Slf4j
@EnableWebSocket
@Configuration(proxyBeanMethods = false)
public class SystemWebSocketConfiguration {

    public SystemWebSocketConfiguration() {
        log.info("SystemWebSocketConfiguration is enabled.");
    }

    @Bean("systemWebSocketManager")
    public ServletWebSocketManager servletWebSocketManager() {
        return new ServletWebSocketManager();
    }

    @Bean
    public SystemWebSocketService systemWebSocketListener(
        TopicManager topicManager,
        @Qualifier("systemWebSocketManager") ServletWebSocketManager servletWebSocketManager
    ) {
        return new SystemWebSocketService(topicManager, servletWebSocketManager);
    }

    @Bean
    public SystemWebSocketHandler systemMessageWebSocketHandler(
        @Qualifier("systemWebSocketManager") ServletWebSocketManager servletWebSocketManager,
        SystemWebSocketService systemWebSocketListener
    ) {
        return new SystemWebSocketHandler(servletWebSocketManager, systemWebSocketListener);
    }

}
