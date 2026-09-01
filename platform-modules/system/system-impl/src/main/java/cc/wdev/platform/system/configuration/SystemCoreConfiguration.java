package cc.wdev.platform.system.configuration;

import cc.wdev.platform.system.core.service.UserSyncRabbitService;
import cc.wdev.platform.system.core.service.impl.UserSyncRabbitServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SystemCoreConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserSyncRabbitService userSyncRabbitService() {
        return new UserSyncRabbitServiceImpl();
    }

}
