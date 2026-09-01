package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.autoconfigure.core.properties.StorageProperties;
import cc.wdev.platform.commons.core.storage.StorageConfig;
import cc.wdev.platform.commons.core.storage.StorageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnClass({S3Client.class})
@ConditionalOnProperty(prefix = StorageProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class StorageAutoConfiguration {

    public StorageAutoConfiguration(StorageProperties properties) {
        log.info("StorageAutoConfiguration is enabled");
        log.info("Current Storage is {}", properties.getType());
        log.info("Aws Storage is {}", properties.getAws().isEnabled());
        log.info("OSS Storage is {}", properties.getOss().isEnabled());
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageFactory storageManager(StorageProperties properties) {
        StorageConfig config = StorageConfig.builder()
            .type(properties.getType())
            .oss(properties.getOss())
            .aws(properties.getAws())
            .build();
        return new StorageFactory(config);
    }

}
