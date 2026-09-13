package cc.wdev.platform.commons.ai.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelProviderConfig implements Serializable {

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    @NestedConfigurationProperty
    private ModelCommonsConfig commons = new ModelCommonsConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private ModelChatConfig chat = new ModelChatConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private ModelEmbeddingConfig embedding = new ModelEmbeddingConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private ModelImageConfig image = new ModelImageConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private ModelTranscriptionConfig transcription = new ModelTranscriptionConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private ModelSpeechConfig speech = new ModelSpeechConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private ModelRerankConfig rerank = new ModelRerankConfig();

}
