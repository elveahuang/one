package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(SensitiveProperties.PREFIX)
public class SensitiveProperties {

    public static final String PREFIX = "platform.sensitive";

    private boolean enabled = true;

    private String aesKey;

}
