package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author elvea
 */
public class AgentScopeTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void reActAgentTest() {
        OpenAIChatModel model = OpenAIChatModel.builder()
            .apiKey(System.getenv("DEEPSEEK_API_KEY"))
            .modelName("deepseek-v4-flash")
            .baseUrl("https://api.deepseek.com")
            .build();

        ReActAgent jarvis = ReActAgent.builder()
            .name("Jarvis")
            .sysPrompt("You are an assistant named Jarvis.")
            .model(model)
            .build();

        // Send message
        Msg msg = Msg.builder()
            .textContent("你好")
            .build();

        Msg response = jarvis.call(msg).block();
        System.out.println(response.getTextContent());
    }

    @Test
    public void harnessAgentTest() {
        OpenAIChatModel model = OpenAIChatModel.builder()
            .apiKey(System.getenv("DEEPSEEK_API_KEY"))
            .modelName("deepseek-v4-flash")
            .baseUrl("https://api.deepseek.com")
            .build();

        Path path = Paths.get(".agentscope/workspace");
        if (!Files.exists(path)) {
            path.toFile().mkdirs();
        }

        HarnessAgent agent = HarnessAgent.builder()
            .name("note-taker")
            .sysPrompt("You are a note-taking assistant.")
            .model(model)
            .workspace(path)
            .compaction(CompactionConfig.builder()
                .triggerMessages(30)
                .keepMessages(10)
                .build())
            .build();

        RuntimeContext ctx = RuntimeContext.builder()
            .sessionId("demo-session")
            .userId("alice")
            .build();

        // Turn 1: introduce yourself + state today's task
        agent.call(new UserMessage("My name is Alice, and I'm preparing a tech talk on ReAct today."), ctx).block();

        // Turn 2: same sessionId — state from turn 1 is restored automatically
        agent.call(new UserMessage("What is my name? What am I doing today?"), ctx).block();
    }

}
