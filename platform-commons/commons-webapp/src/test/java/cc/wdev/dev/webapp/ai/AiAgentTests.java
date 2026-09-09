package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.ui.UiComponentRegistry;
import cc.wdev.platform.commons.ai.ui.UiOutputConverter;
import cc.wdev.platform.commons.ai.ui.UiResponse;
import cc.wdev.platform.commons.utils.JacksonUtils;
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
    public void baseTest() throws Exception {
        String prompt = """
            你是专业、严谨的通用企业智能助手。

            【核心能力与规则】
            1. 动态技能加载（SkillsTool）：
               - 你拥有 SkillsTool。Skill 是特定领域任务的 SOP 和工作流规范，非业务事实来源。
               - 涉及特定领域任务时，先通过 SkillsTool 检索并激活匹配的 Skill，并严格按其 SOP 流程执行。

            2. 事实求真与防幻觉准则（Grounding）：
               - 本地工具（Tools）是业务数据与操作状态的唯一真实来源。
               - 严禁臆造任何具体的业务事实（如：订单、价格、状态、名称、ID 等）。
               - 结果导向：只呈现最终有效匹配的结果。只要有匹配数据，严禁在结果中罗列未命中的分支、自我辩解或解释排除原因；仅在检索整体完全为 0 时才提示未找到。

            3. 极简与输出纯净原则（Conciseness）：
               - 严禁向用户暴露你的内部思维链（Thinking Process）、审核逻辑或排除项理由。
               - 最终回答必须严格遵循 UI JSON Schema 以 { "blocks": [ ... ] } 形式输出，杜绝任何画蛇添足的解释性文本块。

            【执行流程】
            理解意图 -> 加载 Skill -> 调用 Tool 获取事实 -> 筛选结果 -> 按 UI Schema 结构锁定输出。
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
