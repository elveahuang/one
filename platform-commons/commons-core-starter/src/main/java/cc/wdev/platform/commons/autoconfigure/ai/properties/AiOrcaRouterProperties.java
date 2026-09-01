package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.config.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = AiOrcaRouterProperties.PREFIX)
public class AiOrcaRouterProperties {

    public static final String PREFIX = "platform.ai.providers.orcarouter";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private ModelCommonsConfig commons = new ModelCommonsConfig();

    @NestedConfigurationProperty
    private ModelChatConfig chat = new ModelChatConfig();

    @NestedConfigurationProperty
    private ModelEmbeddingConfig embedding = new ModelEmbeddingConfig();

    @NestedConfigurationProperty
    private ModelTranscriptionConfig transcription = new ModelTranscriptionConfig();

    @NestedConfigurationProperty
    private ModelImageConfig image = new ModelImageConfig();

}
