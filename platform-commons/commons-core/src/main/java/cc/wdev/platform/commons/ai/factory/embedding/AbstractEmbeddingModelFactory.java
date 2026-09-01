package cc.wdev.platform.commons.ai.factory.embedding;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractEmbeddingModelFactory implements EmbeddingModelFactory {

    protected final ModelConfig config;

    public AbstractEmbeddingModelFactory(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see EmbeddingModelFactory#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}
