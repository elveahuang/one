package cc.wdev.platform.commons.ai.agent;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ReActAgent} 单元测试：使用桩 ChatModel 与桩工具验证 ReAct 循环，
 * 不依赖任何外部中间件。
 *
 * @author elvea
 */
public class ReActAgentTests {

    /**
     * 桩 ChatModel：按 responder 函数返回响应，并记录每次收到的 Prompt 用于断言消息拼装
     */
    private static class StubChatModel implements ChatModel {

        private final Function<Prompt, ChatResponse> responder;

        private final List<Prompt> prompts = new ArrayList<>();

        private StubChatModel(Function<Prompt, ChatResponse> responder) {
            this.responder = responder;
        }

        @Override
        public ChatResponse call(Prompt prompt) {
            this.prompts.add(prompt);
            return this.responder.apply(prompt);
        }

        @Override
        public Flux<ChatResponse> stream(Prompt prompt) {
            this.prompts.add(prompt);
            return Flux.just(this.responder.apply(prompt));
        }

        @Override
        public ChatOptions getOptions() {
            // 必须返回 ToolCallingChatOptions，ChatClient 才会把工具回调并入请求选项并启动工具调用循环
            return ToolCallingChatOptions.builder().build();
        }

    }

    /**
     * 桩工具：固定返回结果，记录调用次数
     */
    private static class StubToolCallback implements ToolCallback {

        private final String result;

        private int callCount = 0;

        private StubToolCallback(String result) {
            this.result = result;
        }

        @Override
        public ToolDefinition getToolDefinition() {
            return ToolDefinition.builder()
                .name("get_weather")
                .description("查询天气")
                .inputSchema("{\"type\":\"object\"}")
                .build();
        }

        @Override
        public String call(String toolInput) {
            this.callCount++;
            return this.result;
        }

    }

    private static ChatResponse toolCallResponse(String toolName, String args) {
        AssistantMessage message = AssistantMessage.builder()
            .content("")
            .toolCalls(List.of(new AssistantMessage.ToolCall("call-1", "function", toolName, args)))
            .build();
        return ChatResponse.builder().generations(List.of(new Generation(message))).build();
    }

    private static ChatResponse textResponse(String text) {
        return ChatResponse.builder()
            .generations(List.of(new Generation(AssistantMessage.builder().content(text).build())))
            .build();
    }

    private static SimpleChatRequest chatRequest() {
        return SimpleChatRequest.builder()
            .prompt("北京天气怎么样？")
            .conversationId("test-conversation")
            .chatType("AGENT")
            .tenantId(1L)
            .userId(1L)
            .build();
    }

    /**
     * 两跳循环：第一轮返回工具调用，第二轮基于工具结果生成最终回答
     */
    @Test
    public void testToolCallLoop() {
        AtomicInteger round = new AtomicInteger();
        StubChatModel chatModel = new StubChatModel(prompt -> round.incrementAndGet() == 1
            ? toolCallResponse("get_weather", "{\"city\":\"北京\"}")
            : textResponse("北京今天晴。"));
        StubToolCallback tool = new StubToolCallback("晴，25度");
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultSystem("你是天气助手")
            .defaultTools(tool)
            .build();
        ReActAgent agent = ReActAgent.builder()
            .chatClient(chatClient)
            .maxToolCalls(5)
            .conversationHistoryEnabled(true)
            .build();

        AgentResult result = agent.call(chatRequest());

        // 最终回答
        assertEquals("北京今天晴。", result.getContent());
        // 工具执行一次、模型调用两轮
        assertEquals(1, tool.callCount);
        assertEquals(2, chatModel.prompts.size());
        // 事件：tool_call -> tool_result
        assertEquals(2, result.getEvents().size());
        assertEquals("tool_call", result.getEvents().get(0).getType());
        assertEquals("get_weather", result.getEvents().get(0).getToolName());
        assertEquals(1, result.getEvents().get(0).getStep());
        assertEquals("tool_result", result.getEvents().get(1).getType());
        assertEquals("晴，25度", result.getEvents().get(1).getToolResult());
        assertEquals(1, result.getEvents().get(1).getStep());

        // 第二轮消息拼装：[system, user, assistant(toolCalls), toolResponse]
        List<Message> instructions = chatModel.prompts.get(1).getInstructions();
        assertEquals(4, instructions.size());
        assertInstanceOf(SystemMessage.class, instructions.get(0));
        assertInstanceOf(UserMessage.class, instructions.get(1));
        AssistantMessage assistant = assertInstanceOf(AssistantMessage.class, instructions.get(2));
        assertTrue(assistant.hasToolCalls());
        assertInstanceOf(ToolResponseMessage.class, instructions.get(3));
    }

    /**
     * 迭代上限：模型持续要求调用工具，达到 maxToolCalls 后循环被强制中断
     */
    @Test
    public void testMaxToolCallsLimit() {
        StubChatModel chatModel = new StubChatModel(prompt -> toolCallResponse("get_weather", "{}"));
        StubToolCallback tool = new StubToolCallback("晴，25度");
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultSystem("你是天气助手")
            .defaultTools(tool)
            .build();
        ReActAgent agent = ReActAgent.builder()
            .chatClient(chatClient)
            .maxToolCalls(2)
            .conversationHistoryEnabled(true)
            .build();

        AgentResult result = agent.call(chatRequest());

        // 工具执行次数不超过上限，循环终止（不会无限循环）
        assertEquals(2, tool.callCount);
        assertTrue(chatModel.prompts.size() <= 4);
        // 最终回答为超限提示
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("tool call limit"));
    }

    /**
     * 流式输出：START 事件开头、工具事件与文本内容交错、END 事件结尾
     */
    @Test
    public void testStream() {
        AtomicInteger round = new AtomicInteger();
        StubChatModel chatModel = new StubChatModel(prompt -> round.incrementAndGet() == 1
            ? toolCallResponse("get_weather", "{\"city\":\"北京\"}")
            : textResponse("北京今天晴。"));
        StubToolCallback tool = new StubToolCallback("晴，25度");
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultSystem("你是天气助手")
            .defaultTools(tool)
            .build();
        ReActAgent agent = ReActAgent.builder()
            .chatClient(chatClient)
            .maxToolCalls(5)
            .conversationHistoryEnabled(true)
            .build();

        List<String> contents = agent.stream(chatRequest())
            .collectList()
            .block(Duration.ofSeconds(10));

        assertNotNull(contents);
        assertTrue(contents.get(0).contains("[START]"));
        assertTrue(contents.stream().anyMatch(c -> c.contains("tool_call")));
        assertTrue(contents.stream().anyMatch(c -> c.contains("tool_result")));
        assertTrue(contents.stream().anyMatch(c -> c.contains("北京今天晴。")));
        assertTrue(contents.get(contents.size() - 1).contains("[DONE]"));
    }

}
