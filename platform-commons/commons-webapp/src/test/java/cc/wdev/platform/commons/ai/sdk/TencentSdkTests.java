package cc.wdev.platform.commons.ai.sdk;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.webapp.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class TencentSdkTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void baseChatTest() {
        ChatClient chatClient = this.aiManager.getChatModelFactory().getChatClient();
        Assertions.assertNotNull(chatClient);
    }

    @Test
    public void transcriptionTest() {
    }

}
