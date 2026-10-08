package cc.wdev.platform.commons.autoconfigure.core.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(TemplateProperties.PREFIX)
public class TemplateProperties {

    public static final String PREFIX = "platform.template";

    private boolean enabled = false;

}
