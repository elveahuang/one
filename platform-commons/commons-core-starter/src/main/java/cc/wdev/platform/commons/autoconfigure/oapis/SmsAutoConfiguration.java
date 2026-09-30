package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.SmsProperties;
import cc.wdev.platform.commons.oapis.sms.SmsConfig;
import cc.wdev.platform.commons.oapis.sms.SmsFactory;
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
@ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SmsProperties.class})
public class SmsAutoConfiguration {

    public SmsAutoConfiguration(SmsProperties properties) {
        log.info("SmsAutoConfiguration is enabled");
        log.info("Current SMS type is {}", properties.getType());
    }

    @Bean
    public SmsFactory smsFactory(SmsProperties properties) {
        SmsConfig config = SmsConfig.builder()
            .type(properties.getType())
            .aliyun(properties.getAliyun())
            .build();
        return new SmsFactory(config);
    }

}
