package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractTranscriptionModelFactory implements TranscriptionModelFactory {

    protected final ModelConfig config;

    public AbstractTranscriptionModelFactory(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see TranscriptionModelFactory#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}
