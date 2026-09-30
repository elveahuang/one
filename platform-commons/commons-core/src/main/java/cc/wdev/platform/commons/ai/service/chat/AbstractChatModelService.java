package cc.wdev.platform.commons.ai.service.chat;

import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.service.embedding.EmbeddingModelService;

/**
 * @author elvea
 */
public abstract class AbstractChatModelService implements ChatModelService {

    protected final ModelConfig config;

    public AbstractChatModelService(ModelConfig config) {
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
