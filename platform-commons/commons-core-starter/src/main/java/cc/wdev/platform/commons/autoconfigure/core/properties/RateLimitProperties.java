package cc.wdev.platform.commons.autoconfigure.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author elvea
 */
@Data
@Component
@ConfigurationProperties(RateLimitProperties.PREFIX)
public class RateLimitProperties {

    public static final String PREFIX = "platform.rate-limit";

    private boolean enabled = false;

}
