package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(CaptchaProperties.PREFIX)
public class CaptchaProperties {

    public static final String PREFIX = "platform.captcha";

    private boolean enabled = false;

    private String cacheKeyPrefix = "captcha";

}
