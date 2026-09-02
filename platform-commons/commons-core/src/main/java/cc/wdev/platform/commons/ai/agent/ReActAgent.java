package cc.wdev.platform.commons.ai.agent;

import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.commons.ai.domain.chat.SimpleChatContent;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * ReAct 智能体
 * <p>
 * 基于现有 ChatClient 组装结果（模型 + 工具 + Advisor 链）驱动 ReAct 循环：
 * 推理（Reason）由模型完成，行动（Act）由 Spring AI 的 {@link ReActToolCallingAdvisor}
 * 工具调用循环完成，观察（Observation）作为工具结果回填下一轮推理，直至产出最终回答。
 * <p>
 * 设计约束：整个循环必须走 {@link ChatClient} 的 Advisor 链而非裸 ChatModel，
 * 以保证日志、会话记忆（SessionMemoryAdvisor 每轮读写）、长期记忆（AutoMemoryToolsAdvisor）等
 * Advisor 在每一轮循环中继续生效。
 * <p>
 * 输出协议（stream 模式）：以 {@link AiUtils#getStartContent()} 开始，
 * 中间为 {@link SimpleChatContent} JSON 事件（type=thought/tool_call/tool_result/text），
 * 以 {@link AiUtils#getEndContent()} 结束，异常时输出 {@link AiUtils#getErrorContent()}。
 *
 * @author elvea
 */
@Slf4j
public class ReActAgent {

    /**
     * 已组装完整的 ChatClient（含模型、工具与 Advisor 链）
     */
    private final ChatClient chatClient;

    /**
     * 单次对话最大工具调用次数
     */
    private final int maxToolCalls;

    /**
     * 是否由循环 advisor 内部拼接会话历史。
     * 智能体流程总是配合 SessionMemoryAdvisor 使用（其会为每一轮循环注入历史并持久化事件），
     * 因此默认关闭；若 Advisor 链上没有记忆类 Advisor，需显式开启。
     */
    private final boolean conversationHistoryEnabled;

    private ReActAgent(Builder builder) {
        this.chatClient = builder.chatClient;
        this.maxToolCalls = builder.maxToolCalls;
        this.conversationHistoryEnabled = builder.conversationHistoryEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 阻塞式执行：返回最终回答与完整执行事件
     */
    public AgentResult call(SimpleChatRequest request) {
        AgentRunState state = new AgentRunState();
        ChatClient.ChatClientRequestSpec spec = this.prepareSpec(request, state);
        String content = spec.call().content();
        return AgentResult.builder().content(content).events(state.getEvents()).build();
    }

    /**
     * 流式执行：SSE 事件流，思考/工具调用/工具结果事件与最终回答文本按时间顺序输出
     */
    public Flux<String> stream(SimpleChatRequest request) {
        Sinks.Many<AgentEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
        AgentRunState state = new AgentRunState(sink::tryEmitNext);
        ChatClient.ChatClientRequestSpec spec = this.prepareSpec(request, state);

        Flux<String> textFlux = spec.stream().content()
            .map(AiUtils::getTextContent)
            .doFinally(signal -> sink.tryEmitComplete());
        Flux<String> eventFlux = sink.asFlux().map(ReActAgent::toEventContent);

        return Flux.concat(
            Flux.just(AiUtils.getStartContent()),
            Flux.merge(eventFlux, textFlux),
            Flux.defer(() -> Flux.just(AiUtils.getEndContent()))
        ).onErrorResume(e -> {
            log.error("ReAct agent stream error [{}]", request.getConversationId(), e);
            return Flux.just(AiUtils.getErrorContent());
        });
    }

    private ChatClient.ChatClientRequestSpec prepareSpec(SimpleChatRequest request, AgentRunState state) {
        return AiUtils.processChatSpec(this.chatClient, request)
            .advisors(a -> a
                .advisors(new ReActToolCallingAdvisor(this.maxToolCalls, this.conversationHistoryEnabled))
                .param(AiConstants.AGENT_RUN_STATE_CONTEXT_KEY, state));
    }

    private static String toEventContent(AgentEvent event) {
        return GsonUtils.toJson(SimpleChatContent.builder()
            .type(event.getType())
            .content(GsonUtils.toJson(event))
            .build());
    }

    public static class Builder {

        private ChatClient chatClient;

        private int maxToolCalls = AiConstants.MAX_AGENT_TOOL_CALLS;

        private boolean conversationHistoryEnabled = false;

        public Builder chatClient(ChatClient chatClient) {
            this.chatClient = chatClient;
            return this;
        }

        public Builder maxToolCalls(int maxToolCalls) {
            this.maxToolCalls = maxToolCalls;
            return this;
        }

        public Builder conversationHistoryEnabled(boolean conversationHistoryEnabled) {
            this.conversationHistoryEnabled = conversationHistoryEnabled;
            return this;
        }

        public ReActAgent build() {
            Assert.notNull(this.chatClient, "chatClient must not be null");
            Assert.isTrue(this.maxToolCalls > 0, "maxToolCalls must be greater than 0");
            return new ReActAgent(this);
        }

    }

}
