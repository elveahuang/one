package cc.wdev.webapp.ai.service;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.webapp.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class AiServiceTests extends BaseTests {

    @Autowired
    private AiService aiService;

    @Test
    public void baseTest() {
        SimpleChatRequest request = SimpleChatRequest.builder()
            .chatType(AiChatType.STATIC.getValue())
            .prompt("你好")
            .build();
        String resp = this.aiService.chatText(request);
        Assertions.assertNotNull(resp);
    }

}
