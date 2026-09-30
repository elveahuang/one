package cc.wdev.platform.commons.ai.factory.image;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractImageModelFactory implements ImageModelFactory {

    protected final ModelConfig config;

    public AbstractImageModelFactory(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see ImageModelFactory#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}
