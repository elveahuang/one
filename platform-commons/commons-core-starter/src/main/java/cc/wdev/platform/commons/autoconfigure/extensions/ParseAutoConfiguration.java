package cc.wdev.platform.commons.autoconfigure.extensions;

import cc.wdev.platform.commons.autoconfigure.extensions.properties.ParseProperties;
import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({ParseProperties.class})
public class ParseAutoConfiguration {

    private final ParseProperties properties;

    public ParseAutoConfiguration(ParseProperties properties) {
        log.info("ParseAutoConfiguration is enabled");
        this.properties = properties;

        // 设置 JavaCV 调试日志
        if (this.properties.getDebug().isEnabled()) {
            System.setProperty("org.bytedeco.javacpp.logger.debug", "true");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ParseManager parseManager() {
        return new ParseManager(
            ParseConfig.builder()
                .enabled(this.properties.isEnabled())
                .debug(this.properties.getDebug())
                .tesseract(this.properties.getTesseract())
                .document(this.properties.getDocument())
                .media(this.properties.getMedia())
                .build()
        );
    }

}
