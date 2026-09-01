package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.WeiXinCpProperties;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.weixin.config.AppCpConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxCpManager;
import cc.wdev.platform.commons.oapis.weixin.service.impl.WxCpManagerImpl;
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
@EnableConfigurationProperties({WeiXinCpProperties.class})
@ConditionalOnProperty(prefix = WeiXinCpProperties.PREFIX, name = "enabled", havingValue = "true")
public class WeiXinCpAutoConfiguration {

    public WeiXinCpAutoConfiguration() {
        log.info("WeiXinCpAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean(WxCpManager.class)
    public WxCpManager wxCpManager(WeiXinCpProperties properties, CacheService cacheService) {
        // 默认企业微信应用配置信息
        AppCpConfig appConfig = AppCpConfig.builder()
            .corpId(properties.getCorpId())
            .corpSecret(properties.getCorpSecret())
            .agentId(properties.getAgentId())
            .token(properties.getToken())
            .build();
        // 创建企业微信服务实例
        WxCpManagerImpl manager = new WxCpManagerImpl(cacheService, properties.getCacheKeyPrefix());
        manager.setAppConfig(appConfig);
        return manager;
    }

}
