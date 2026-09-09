package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.ui.UiComponentRegistry;
import cc.wdev.platform.commons.ai.ui.UiOutputConverter;
import cc.wdev.platform.commons.ai.ui.UiResponse;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.webapp.ai.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class AiAgentTests extends BaseTests {

    @Autowired
    private AiService aiService;

    @Autowired
    private UiComponentRegistry registry;

    @Test
    public void baseTest() throws Exception {
        UiOutputConverter converter = new UiOutputConverter(registry.buildJsonSchema());
        ChatClient client = this.aiService.getChatClient();

        UiResponse response = client.prompt()
            .user("有没有数学相关的课程")
//            .user("有没有名字叫张三的讲师")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "spring-ai-session"))
            .call()
            .entity(converter, ChatClient.EntityParamSpec::validateSchema);
        Assertions.assertNotNull(response);

        String json = JacksonUtils.toJson(response);
        Assertions.assertNotNull(json);
    }

}
