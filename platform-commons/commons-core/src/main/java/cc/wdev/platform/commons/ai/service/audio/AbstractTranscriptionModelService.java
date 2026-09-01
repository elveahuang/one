package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.model.ModelConfig;

/**
 * @author elvea
 */
public abstract class AbstractTranscriptionModelService implements TranscriptionModelService {

    protected final ModelConfig config;

    public AbstractTranscriptionModelService(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see TranscriptionModelService#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }
}
