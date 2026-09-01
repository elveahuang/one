package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.LarkProperties;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.lark.cache.Cache;
import cc.wdev.platform.commons.oapis.lark.cache.RedisCache;
import cc.wdev.platform.commons.oapis.lark.config.AppConfig;
import cc.wdev.platform.commons.oapis.lark.service.LarkService;
import cc.wdev.platform.commons.oapis.lark.service.impl.LarkServiceImpl;
import com.lark.oapi.Client;
import com.lark.oapi.core.cache.ICache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({LarkProperties.class})
@ConditionalOnClass({ICache.class, Client.class})
@ConditionalOnProperty(prefix = LarkProperties.PREFIX, name = "enabled", havingValue = "true")
public class LarkAutoConfiguration {

    public LarkAutoConfiguration() {
        log.info("LarkAutoConfiguration is enabled");
    }

    @Bean("larkCache")
    @ConditionalOnMissingBean(Cache.class)
    public Cache larkCache(CacheService cacheService, LarkProperties properties) {
        return new RedisCache(cacheService, properties.getCacheKeyPrefix());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Cache.class)
    public LarkService larkService(Cache cache, LarkProperties properties) {
        AppConfig appConfig = AppConfig.builder()
            .appType(properties.getAppType())
            .appId(properties.getAppId())
            .appSecret(properties.getAppSecret())
            .verificationToken(properties.getVerificationToken())
            .encryptKey(properties.getEncryptKey())
            .debug(properties.isDebug())
            .cache(cache)
            .build();
        LarkServiceImpl service = new LarkServiceImpl(cache, properties.getCacheKeyPrefix());
        service.setAppConfig(appConfig);
        return service;
    }

}
