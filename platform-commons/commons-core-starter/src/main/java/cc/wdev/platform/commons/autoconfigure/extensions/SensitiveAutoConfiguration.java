package cc.wdev.platform.commons.autoconfigure.extensions;

import cc.wdev.platform.commons.autoconfigure.extensions.properties.SensitiveProperties;
import cc.wdev.platform.commons.extensions.sensitive.DefaultSensitiveService;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveConfig;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveService;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static cc.wdev.platform.commons.extensions.sensitive.DefaultSensitiveService.DEFAULT_AES_KEY;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = SensitiveProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({SensitiveProperties.class})
public class SensitiveAutoConfiguration {

    public SensitiveAutoConfiguration() {
        log.info("SensitiveAutoConfiguration is enabled.");
    }

    @Bean
    @ConditionalOnMissingBean
    public SensitiveService sensitiveService(SensitiveProperties properties) {
        String aesKey = StringUtils.isNotEmpty(properties.getAesKey()) ? properties.getAesKey() : DEFAULT_AES_KEY;
        return new DefaultSensitiveService(SensitiveConfig.builder().aesKey(aesKey).build());
    }

}
