package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.ui.UiComponentRegistry;
import cc.wdev.platform.commons.ai.ui.UiOutputConverter;
import cc.wdev.platform.commons.ai.ui.UiResponse;
import cc.wdev.webapp.ai.tools.CoreTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class AiAgentTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private UiComponentRegistry registry;

    @Autowired
    private CoreTools coreTools;

    @Test
    public void baseTest() {
        String prompt = """
            你是智能学习助手。

            你拥有 SkillsTool。Skill 是领域任务的规则和工作流，不是业务事实来源。
            根据用户请求，按需加载 course-recommendation 或 exam-recommendation Skill。
            Skill 激活后，严格遵循其流程，并使用 Skill 指定的搜索工具。

            搜索工具返回候选集。候选集是真实业务数据；你必须基于候选集进行审核、筛选、排序。
            不允许编造课程、考试、价格、老师或 ID。

            最终必须输出符合提供的 UI JSON Schema 的 blocks。
            """;

        ChatModel model = this.aiManager.getChatModel();
        Assertions.assertNotNull(model);

        UiOutputConverter converter = new UiOutputConverter(registry.buildJsonSchema());

        ChatClient.Builder builder = ChatClient.builder(model);
        this.aiManager.applyBaseAdvisors(builder);
        this.aiManager.applyMemoryAdvisor(builder);
        this.aiManager.applyAgentTool(builder);
        builder.defaultTools(coreTools);

        ChatClient client = builder.build();
        UiResponse response = client.prompt()
            .system(prompt)
            .user("有没有数学相关课程")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "spring-ai-session"))
            .call()
            .entity(converter, ChatClient.EntityParamSpec::validateSchema);
        Assertions.assertNotNull(response);
    }

}
