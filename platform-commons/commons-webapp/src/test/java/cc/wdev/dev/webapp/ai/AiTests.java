package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.tools.CommonTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class AiTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private CommonTools commonTools;

    /**
     * 测试会话记忆
     */
    @Test
    public void sessionTest() {
        Assertions.assertNotNull(this.aiManager);

        ChatClient chatClient = ChatClient.builder(this.aiManager.getChatModel())
            .defaultAdvisors(this.aiManager.getSessionMemoryAdvisor())
            .defaultTools(this.commonTools)
            .build();

        ChatResponse response = chatClient
            .prompt()
            .user("查询系统时间")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "spring-ai-session"))
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

    @Test
    public void baseToolTest() {
        ChatModel model = aiManager.getChatModelFactory().getChatModel();
        String content = ChatClient.create(model)
            .prompt("你好，现在时间是？")
            .call()
            .content();
        Assertions.assertNotNull(content);
    }

    @Test
    public void baseBookTest() {
        ChatModel model = aiManager.getChatModelFactory().getChatModel();
        String content = ChatClient.create(model)
            .prompt("有没有古典文学书籍介绍")
            .call()
            .content();
        Assertions.assertNotNull(content);
    }

    @Test
    public void toolNamesTest() {
        ChatModel model = aiManager.getChatModelFactory().getChatModel();
        String content = ChatClient.create(model)
            .prompt("跟李四")
            .call()
            .content();
        Assertions.assertNotNull(content);
    }

}
