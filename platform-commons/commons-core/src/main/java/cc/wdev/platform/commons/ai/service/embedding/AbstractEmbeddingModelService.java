package cc.wdev.platform.commons.ai.service.embedding;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractEmbeddingModelService implements EmbeddingModelService {

    protected final ModelConfig config;

    public AbstractEmbeddingModelService(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see EmbeddingModelService#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}
