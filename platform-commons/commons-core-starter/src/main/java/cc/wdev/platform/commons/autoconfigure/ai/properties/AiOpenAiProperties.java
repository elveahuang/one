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
@ConfigurationProperties(prefix = AiOpenAiProperties.PREFIX)
public class AiOpenAiProperties {

    public static final String PREFIX = "platform.ai.providers.openai";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private ModelCommonsConfig commons = new ModelCommonsConfig();

    @NestedConfigurationProperty
    private ModelChatConfig chat = new ModelChatConfig();

    @NestedConfigurationProperty
    private ModelEmbeddingConfig embedding = new ModelEmbeddingConfig();

    @NestedConfigurationProperty
    private ModelImageConfig image = new ModelImageConfig();

    @NestedConfigurationProperty
    private ModelTranscriptionConfig translation = new ModelTranscriptionConfig();

    @NestedConfigurationProperty
    private ModelSpeechConfig speech = new ModelSpeechConfig();

}
