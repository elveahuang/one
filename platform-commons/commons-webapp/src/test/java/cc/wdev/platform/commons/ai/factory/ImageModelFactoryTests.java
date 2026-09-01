package cc.wdev.platform.commons.ai.factory;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class ImageModelFactoryTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiManager);
    }

    @Test
    public void deepseekChatTest() {
        ChatModel chatModel = this.aiManager.getChatModelFactory(AiServiceProvider.SPRING_AI_DEEPSEEK).getChatModel();
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Assertions.assertNotNull(chatModel);

        ChatResponse response = chatClient.prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

    @Test
    public void openaiChatTest() {
        ChatModel chatModel = this.aiManager.getChatModelFactory(AiServiceProvider.SPRING_AI_OPENAI).getChatModel();
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Assertions.assertNotNull(chatModel);

        ChatResponse response = chatClient.prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

}
