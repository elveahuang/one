package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractSpeechModelFactory implements SpeechModelFactory {

    protected final ModelConfig config;

    public AbstractSpeechModelFactory(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see SpeechModelFactory#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}
