package cc.wdev.platform.commons.ai.factory.chat;

import cc.wdev.platform.commons.ai.model.ModelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

/**
 * @author elvea
 */
@Slf4j
public abstract class AbstractChatModelFactory implements ChatModelFactory {

    protected final ModelConfig config;

    public AbstractChatModelFactory(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see ChatModelFactory#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

    /**
     * @see ChatModelFactory#getChatClient(ModelConfig)
     */
    @Override
    public ChatClient getChatClient(ModelConfig config) {
        log.info("Create ChatClient: {}", config.getName());
        return ChatClient.builder(this.getModel(config)).build();
    }

}
