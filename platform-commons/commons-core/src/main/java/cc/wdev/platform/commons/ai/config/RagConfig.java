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
public class RagConfig implements Serializable {

    @Builder.Default
    private boolean enabled = false;

    @Builder.Default
    @NestedConfigurationProperty
    private VectorStoreConfig store = new VectorStoreConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private SplittingConfig splitting = new SplittingConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private VectorizationConfig vectorization = new VectorizationConfig();

    @Builder.Default
    @NestedConfigurationProperty
    private RetrievalConfig retrieval = new RetrievalConfig();

}
