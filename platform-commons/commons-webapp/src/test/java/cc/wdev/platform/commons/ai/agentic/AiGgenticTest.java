package cc.wdev.platform.commons.ai.agentic;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import io.github.agentic.spring.ai.graph.agent.ReactAgent;
import io.github.agentic.spring.ai.graph.exception.GraphRunnerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AiGgenticTest extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiManager);
    }

    @Test
    public void openAiTest() throws GraphRunnerException {
        ReactAgent agent = ReactAgent.builder()
            .name("Agent")
            .systemPrompt("你是一个专业的技术助手。请准确、简洁地回答问题。")
            .model(this.aiManager.getChatModel())
            .build();

        String text = agent.call("杭州的天气怎么样？").getText();
        Assertions.assertNotNull(text);
    }

}
