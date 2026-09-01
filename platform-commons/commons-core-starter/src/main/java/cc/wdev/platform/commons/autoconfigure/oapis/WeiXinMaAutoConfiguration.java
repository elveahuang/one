package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.WeiXinMaProperties;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.weixin.config.AppMaConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxMaManager;
import cc.wdev.platform.commons.oapis.weixin.service.impl.WxMaManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({WeiXinMaProperties.class})
@ConditionalOnProperty(prefix = WeiXinMaProperties.PREFIX, name = "enabled", havingValue = "true")
public class WeiXinMaAutoConfiguration {

    public WeiXinMaAutoConfiguration() {
        log.info("WeiXinMaAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean(WxMaManager.class)
    public WxMaManager wxMaManager(WeiXinMaProperties properties, CacheService cacheService) {
        // 默认微信小程序应用配置信息
        AppMaConfig appConfig = AppMaConfig.builder()
            .appId(properties.getAppId())
            .appSecret(properties.getAppSecret())
            .build();
        // 创建微信小程序服务实例
        WxMaManagerImpl manager = new WxMaManagerImpl(cacheService, properties.getCacheKeyPrefix());
        manager.setAppConfig(appConfig);
        return manager;
    }

}
