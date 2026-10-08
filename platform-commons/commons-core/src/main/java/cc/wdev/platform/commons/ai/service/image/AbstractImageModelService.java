package cc.wdev.platform.commons.ai.service.image;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractImageModelService implements ImageModelService {

    protected final ModelConfig config;

    public AbstractImageModelService(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see ImageModelService#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }
}
