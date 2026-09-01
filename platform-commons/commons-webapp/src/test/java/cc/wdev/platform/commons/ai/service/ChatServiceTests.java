package cc.wdev.platform.commons.ai.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleChatResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.chat.ChatModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class ChatServiceTests extends BaseTests {

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void openAiTest() {
        ChatModelService service = this.aiServiceManager.getChatModelService(AiServiceProvider.OPENAI_SDK);
        Assertions.assertNotNull(service);

        SimpleChatRequest request = SimpleChatRequest.builder()
            .systemPrompt("你是一位专业的助手")
            .prompt("你好")
            .build();
        SimpleChatResponse<?> response = service.call(request);
        Assertions.assertNotNull(response);
    }

}
