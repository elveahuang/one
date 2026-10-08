package cc.wdev.platform.commons.ai.factory.chat;

import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

/**
 * @author elvea
 */
public interface ChatModelFactory extends ModelFactory<ChatModel> {

    /**
     * @see ModelFactory#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.TEXT;
    }

    /**
     * 获取智能对话客户端
     */
    default ChatClient getChatClient() {
        return this.getChatClient(this.getModelConfig());
    }

    /**
     * 获取智能对话客户端
     */
    ChatClient getChatClient(ModelConfig config);

    /**
     * 获取智能对话模型
     */
    default ChatModel getChatModel() {
        return this.getChatModel(this.getModelConfig());
    }

    /**
     * 获取智能对话模型
     */
    default ChatModel getChatModel(ModelConfig config) {
        return this.getModel(config);
    }

}
