package cc.wdev.platform.commons.autoconfigure.extensions;

import cc.wdev.platform.commons.autoconfigure.extensions.properties.SeleniumProperties;
import cc.wdev.platform.commons.extensions.selenium.DefaultSeleniumFactory;
import cc.wdev.platform.commons.extensions.selenium.SeleniumConfig;
import cc.wdev.platform.commons.extensions.selenium.SeleniumFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.manager.SeleniumManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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
@EnableConfigurationProperties({SeleniumProperties.class})
@ConditionalOnClass({SeleniumManager.class})
@ConditionalOnProperty(prefix = SeleniumProperties.PREFIX, name = "enabled", havingValue = "true")
public class SeleniumAutoConfiguration {

    public SeleniumAutoConfiguration() {
        log.info("SeleniumAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean
    public SeleniumFactory seleniumFactory(SeleniumProperties properties) {
        SeleniumConfig config = SeleniumConfig.builder()
            .driverType(properties.getDriverType())
            .driverVersion(properties.getDriverVersion())
            .proxy(properties.getProxy())
            .build();
        SeleniumFactory factory = new DefaultSeleniumFactory(config);
        factory.initialize();
        return factory;
    }

}
