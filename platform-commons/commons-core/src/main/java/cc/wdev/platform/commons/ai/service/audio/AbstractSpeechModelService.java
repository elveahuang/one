package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractSpeechModelService implements SpeechModelService {

    protected final ModelConfig config;

    public AbstractSpeechModelService(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see SpeechModelService#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}
