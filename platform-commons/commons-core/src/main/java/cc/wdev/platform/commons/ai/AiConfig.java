package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.config.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConfig implements Serializable {

    /**
     * 是否启用回退到默认模型
     * 默认不启用，启用后会优先使用当前配置的模型，如果当前模型不存在，则会回退到默认模型
     */
    private boolean fallbackEnabled = false;

    private ServiceProviderConfig service;

    private ServiceProviderConfig factory;

    private VectorStoreConfig vectorStore;

    private SplittingConfig splitting;

    private RetrievalConfig retrieval;

    private VectorizationConfig vectorization;

    @Builder.Default
    private AgentConfig agent = AgentConfig.builder().build();

    @Builder.Default
    private MemoryConfig memory = MemoryConfig.builder().build();

}
