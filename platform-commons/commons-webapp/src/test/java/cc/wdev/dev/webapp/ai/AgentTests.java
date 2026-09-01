package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.webapp.ai.tools.CoreTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class AgentTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private CoreTools coreTools;

    @Test
    public void baseTest() {
        ChatModel model = aiManager.getChatModelFactory().getChatModel();
        String content = ChatClient.create(model).prompt("你好，帮我推荐经济相关的课程").tools(coreTools).call().content();
        Assertions.assertNotNull(content);
    }

}
