package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.TranslatorProperties;
import cc.wdev.platform.commons.oapis.translator.TranslatorConfig;
import cc.wdev.platform.commons.oapis.translator.TranslatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({TranslatorProperties.class})
@ConditionalOnProperty(prefix = TranslatorProperties.PREFIX, name = "enabled", havingValue = "true")
public class TranslatorAutoConfiguration {

    public TranslatorAutoConfiguration(TranslatorProperties properties) {
        log.info("TranslatorAutoConfiguration is enabled");
        log.info("Current Translator service is {}", properties.getType());
    }

    @Bean
    public TranslatorFactory translatorFactory(TranslatorProperties properties) {
        TranslatorConfig config = TranslatorConfig.builder()
            .enabled(properties.isEnabled())
            .type(properties.getType())
            .aliyun(properties.getAliyun())
            .build();
        return new TranslatorFactory(config);
    }

}
