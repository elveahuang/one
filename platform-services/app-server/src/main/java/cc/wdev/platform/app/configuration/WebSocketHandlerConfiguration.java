package cc.wdev.platform.app.configuration;

import cc.wdev.platform.commons.message.socket.servlet.interceptor.WebSocketSessionInterceptor;
import cc.wdev.platform.system.core.socket.SystemWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@AllArgsConstructor
@EnableWebSocketSecurity
@Configuration(proxyBeanMethods = false)
public class WebSocketHandlerConfiguration implements WebSocketConfigurer {

    private final SystemWebSocketHandler systemWebSocketHandler;

    private final WebSocketSessionInterceptor webSocketSessionInterceptor;

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        log.info("Registry messageWebSocketHandler to path [{}]", "/ws/message");
        registry.addHandler(systemWebSocketHandler, "/ws/message")
            .setAllowedOrigins("*")
            .addInterceptors(webSocketSessionInterceptor);
    }

}
