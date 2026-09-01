package cc.wdev.platform.commons.autoconfigure.core.properties;

import cc.wdev.platform.commons.core.log.config.LogConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(LogProperties.PREFIX)
public class LogProperties {

    public static final String PREFIX = "platform.log";

    private boolean enabled = true;

    @NestedConfigurationProperty
    private LogConfig config = LogConfig.builder().build();

}
