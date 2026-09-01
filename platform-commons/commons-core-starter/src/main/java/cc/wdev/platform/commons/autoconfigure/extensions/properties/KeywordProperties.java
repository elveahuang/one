package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(KeywordProperties.PREFIX)
public class KeywordProperties {

    public static final String PREFIX = "platform.keyword";

    private boolean enabled = true;

}
