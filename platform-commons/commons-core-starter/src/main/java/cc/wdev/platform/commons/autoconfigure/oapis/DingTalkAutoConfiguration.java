package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.DingTalkProperties;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.dingtalk.cache.Cache;
import cc.wdev.platform.commons.oapis.dingtalk.cache.DefaultCache;
import cc.wdev.platform.commons.oapis.dingtalk.config.AppConfig;
import cc.wdev.platform.commons.oapis.dingtalk.service.DingTalkService;
import cc.wdev.platform.commons.oapis.dingtalk.service.impl.DingTalkServiceImpl;
import com.aliyun.dingtalkoauth2_1_0.Client;
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
@EnableConfigurationProperties({DingTalkProperties.class})
@ConditionalOnClass({Client.class})
@ConditionalOnProperty(prefix = DingTalkProperties.PREFIX, name = "enabled", havingValue = "true")
public class DingTalkAutoConfiguration {

    public DingTalkAutoConfiguration() {
        log.info("DingTalkAutoConfiguration is enabled");
    }

    @Bean("dingtalkCache")
    @ConditionalOnMissingBean(Cache.class)
    public Cache dingtalkCache(CacheService cacheService, DingTalkProperties properties) {
        return new DefaultCache(cacheService, properties.getCacheKeyPrefix());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Cache.class)
    public DingTalkService dingTalkService(Cache cache, DingTalkProperties properties) {
        AppConfig appConfig = AppConfig.builder()
            .corpId(properties.getCorpId())
            .agentId(properties.getAgentId())
            .appKey(properties.getAppKey())
            .appSecret(properties.getAppSecret())
            .build();
        DingTalkServiceImpl service = new DingTalkServiceImpl(cache, properties.getCacheKeyPrefix());
        service.setAppConfig(appConfig);
        return service;
    }

}
