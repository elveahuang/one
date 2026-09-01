package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = AiDeepSeekProperties.PREFIX)
public class AiDeepSeekProperties {

    public static final String PREFIX = "platform.ai.providers.deepseek";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private ModelCommonsConfig commons = new ModelCommonsConfig();

    @NestedConfigurationProperty
    private ModelChatConfig chat = new ModelChatConfig();

}
