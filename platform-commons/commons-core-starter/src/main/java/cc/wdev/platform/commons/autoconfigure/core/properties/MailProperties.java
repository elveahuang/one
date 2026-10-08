package cc.wdev.platform.commons.autoconfigure.core.properties;

import cc.wdev.platform.commons.core.mail.MailConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(MailProperties.PREFIX)
public class MailProperties {

    public static final String PREFIX = "platform.mail";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private MailConfig config = new MailConfig();

}
