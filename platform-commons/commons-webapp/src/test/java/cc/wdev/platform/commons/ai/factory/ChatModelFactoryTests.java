package cc.wdev.platform.commons.ai.factory;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.chat.ChatModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.webapp.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

import static cc.wdev.platform.commons.ai.enums.AiServiceProvider.SPRING_AI_DEEPSEEK;

/**
 * @author elvea
 */
public class ChatModelFactoryTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiManager);
    }

    @Test
    public void deepseekChatTest() {
        ChatClient chatClient = ChatClient.builder(this.aiManager.getChatModel(SPRING_AI_DEEPSEEK))
            .defaultAdvisors(this.aiManager.getSessionMemoryAdvisor())
            .build();

        ChatResponse response = chatClient.prompt()
            .user("你好")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "spring-ai-session"))
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);

        String text = AiUtils.getChatResponseContent(response);
        Assertions.assertNotNull(text);
    }

    @Test
    public void openaiChatTest() {
        ChatClient chatClient = this.aiManager.getChatModelFactory(AiServiceProvider.SPRING_AI_OPENAI).getChatClient();
        Assertions.assertNotNull(chatClient);

        ChatResponse response = chatClient.prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

    @Test
    public void orcaRouterChatTest() {
        ChatClient chatClient = this.aiManager.getChatModelFactory(AiServiceProvider.SPRING_AI_OPENAI).getChatClient();
        Assertions.assertNotNull(chatClient);

        ChatResponse response = chatClient.prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

    @Test
    public void defaultChatClientTest() {
        ChatClient chatClient = this.aiManager.getChatClient();
        Assertions.assertNotNull(chatClient);
    }

    @Test
    public void dynamicProviderChatClientTest() {
        ModelConfig config = SimpleModelConfig.builder()
            .modelProvider("siliconflow")
            .serviceProvider("spring-ai-openai")
            .name("deepseek-ai/DeepSeek-V3")
            .baseUrl("https://api.siliconflow.cn/v1")
            .apiKey("sk-mock-test-key")
            .build();
        ChatClient chatClient = this.aiManager.getChatClient(config);
        Assertions.assertNotNull(chatClient);
    }

    @Test
    public void anthropicChatTest() {
        ChatModelFactory factory = this.aiManager.getChatModelFactory(AiServiceProvider.SPRING_AI_ANTHROPIC);
        Assertions.assertNotNull(factory);

        ModelConfig config = SimpleModelConfig.builder()
            .modelProvider("anthropic")
            .serviceProvider("spring-ai-anthropic")
            .name("claude-3-7-sonnet")
            .baseUrl("https://api.anthropic.com")
            .apiKey("sk-ant-mock-key")
            .build();
        ChatModel chatModel = factory.getModel(config);
        Assertions.assertNotNull(chatModel);

        ChatClient chatClient = this.aiManager.getChatClient(config);
        Assertions.assertNotNull(chatClient);
    }

}
