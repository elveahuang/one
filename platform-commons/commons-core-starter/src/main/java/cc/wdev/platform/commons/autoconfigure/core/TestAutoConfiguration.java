package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.autoconfigure.core.properties.TestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({TestProperties.class})
@ConditionalOnProperty(prefix = TestProperties.PREFIX, name = "enabled", havingValue = "true")
public class TestAutoConfiguration {

    public TestAutoConfiguration(TestProperties properties) {
        log.info("TestAutoConfiguration is enabled");
        log.info("Test email - {}", properties.getEmail());
        log.info("Test mobile country code - {}", properties.getMobileCountryCode());
        log.info("Test mobile number - {}", properties.getMobileNumber());
    }

}
