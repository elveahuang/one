package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.FaceBodyProperties;
import cc.wdev.platform.commons.oapis.facebody.FaceBodyConfig;
import cc.wdev.platform.commons.oapis.facebody.FaceBodyFactory;
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
@EnableConfigurationProperties({FaceBodyProperties.class})
@ConditionalOnProperty(prefix = FaceBodyProperties.PREFIX, name = "enabled", havingValue = "true")
public class FaceBodyAutoConfiguration {

    public FaceBodyAutoConfiguration(FaceBodyProperties properties) {
        log.info("FaceBodyAutoConfiguration is enabled");
        log.info("Current FaceBody service is {}", properties.getType());
    }

    @Bean
    @ConditionalOnMissingBean
    public FaceBodyFactory faceBodyFactory(FaceBodyProperties properties) {
        FaceBodyConfig config = FaceBodyConfig.builder()
            .enabled(properties.isEnabled())
            .type(properties.getType())
            .aliyun(properties.getAliyun())
            .build();
        return new FaceBodyFactory(config);
    }

}
