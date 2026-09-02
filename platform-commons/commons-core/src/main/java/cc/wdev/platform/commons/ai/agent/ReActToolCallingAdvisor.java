package cc.wdev.platform.commons.ai.agent;

import cc.wdev.platform.commons.ai.AiConstants;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.tool.ToolCallingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReAct 循环 Advisor
 * <p>
 * 继承 Spring AI 的 {@link ToolCallingAdvisor}（框架自带的工具调用循环），
 * 利用其每轮循环的扩展钩子把 ReAct 过程事件化：
 * <ul>
 * <li>轮次开始（doBeforeCall/doBeforeStream）：轮次计数 +1，并从本轮上下文中提取
 * 上一轮的工具执行结果，发射 TOOL_RESULT 事件；</li>
 * <li>轮次结束（doAfterCall/doAfterStream）：若模型响应包含工具调用，则发射
 * THOUGHT（思考文本）与 TOOL_CALL 事件；若不包含，则为最终回答轮，由调用方流式输出。</li>
 * </ul>
 * <p>
 * 工具调用次数上限由 {@link ToolCallingManager#builder()} 的 maxTotalToolCalls 控制，
 * 超限后框架直接中断循环并向客户端返回超限提示。
 * <p>
 * 运行状态通过 advisor context 传递（{@link AiConstants#AGENT_RUN_STATE_CONTEXT_KEY}），
 * advisor 实例本身无状态，可安全复用。
 *
 * @author elvea
 */
@Slf4j
public class ReActToolCallingAdvisor extends ToolCallingAdvisor {

    private final int maxToolCalls;

    private final boolean conversationHistoryEnabled;

    public ReActToolCallingAdvisor(int maxToolCalls, boolean conversationHistoryEnabled) {
        super(ToolCallingManager.builder().maxTotalToolCalls(maxToolCalls).build(),
            DEFAULT_TOOL_EXECUTION_ELIGIBILITY_CHECKER, DEFAULT_ORDER, conversationHistoryEnabled);
        this.maxToolCalls = maxToolCalls;
        this.conversationHistoryEnabled = conversationHistoryEnabled;
    }

    public int getMaxToolCalls() {
        return this.maxToolCalls;
    }

    public boolean isConversationHistoryEnabled() {
        return this.conversationHistoryEnabled;
    }

    // ------------------------------------------------------------------------------
    // 非流式循环钩子
    // ------------------------------------------------------------------------------

    @Override
    protected ChatClientRequest doBeforeCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        this.onRoundStart(request);
        return request;
    }

    @Override
    protected ChatClientResponse doAfterCall(@NonNull ChatClientResponse response, @NonNull CallAdvisorChain chain) {
        this.onRoundEnd(response.context(), response.chatResponse());
        return response;
    }

    // ------------------------------------------------------------------------------
    // 流式循环钩子
    // ------------------------------------------------------------------------------

    @Override
    protected ChatClientRequest doBeforeStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        this.onRoundStart(request);
        return request;
    }

    @Override
    protected ChatClientResponse doAfterStream(@NonNull ChatClientResponse response, @NonNull StreamAdvisorChain chain) {
        this.onRoundEnd(response.context(), response.chatResponse());
        return response;
    }

    // ------------------------------------------------------------------------------
    // 事件提取
    // ------------------------------------------------------------------------------

    /**
     * 轮次开始：步数 +1，提取上一轮的工具执行结果
     */
    private void onRoundStart(ChatClientRequest request) {
        AgentRunState state = resolveState(request.context());
        if (state == null) {
            return;
        }
        state.beginStep();
        this.emitToolResults(state, request.prompt().getInstructions());
    }

    /**
     * 轮次结束：若响应携带工具调用，发射思考与工具调用事件
     */
    private void onRoundEnd(Map<String, Object> context, @Nullable ChatResponse chatResponse) {
        AgentRunState state = resolveState(context);
        if (state == null || chatResponse == null || !chatResponse.hasToolCalls()) {
            return;
        }
        int step = state.currentStep();
        AssistantMessage output = chatResponse.getResult().getOutput();
        String text = output.getText();
        if (text != null && !text.isBlank()) {
            state.emit(AgentEvent.thought(step, truncate(text)));
        }
        for (AssistantMessage.ToolCall toolCall : output.getToolCalls()) {
            state.emit(AgentEvent.toolCall(step, toolCall.name(), truncate(toolCall.arguments())));
        }
    }

    /**
     * 提取本轮尚未发射的工具响应并发射 TOOL_RESULT 事件。
     * 工具结果逻辑上归属发起调用的轮次（当前轮次 - 1）。
     */
    private void emitToolResults(AgentRunState state, List<Message> instructions) {
        List<ToolResponseMessage> messages = findCurrentTurnToolResponses(instructions);
        int emitted = state.getEmittedToolResponses();
        if (messages.size() <= emitted) {
            return;
        }
        int step = Math.max(1, state.currentStep() - 1);
        for (int i = emitted; i < messages.size(); i++) {
            for (ToolResponseMessage.ToolResponse response : messages.get(i).getResponses()) {
                state.emit(AgentEvent.toolResult(step, response.name(), truncate(response.responseData())));
            }
        }
        state.setEmittedToolResponses(messages.size());
    }

    /**
     * 收集当前轮次（最后一条用户消息及之后）的工具响应消息
     */
    private List<ToolResponseMessage> findCurrentTurnToolResponses(List<Message> instructions) {
        List<ToolResponseMessage> list = new ArrayList<>();
        int start = 0;
        for (int i = instructions.size() - 1; i >= 0; i--) {
            if (instructions.get(i) instanceof UserMessage) {
                start = i;
                break;
            }
        }
        for (int i = start; i < instructions.size(); i++) {
            if (instructions.get(i) instanceof ToolResponseMessage toolResponseMessage) {
                list.add(toolResponseMessage);
            }
        }
        return list;
    }

    @Nullable
    private AgentRunState resolveState(Map<String, Object> context) {
        if (context == null) {
            return null;
        }
        Object value = context.get(AiConstants.AGENT_RUN_STATE_CONTEXT_KEY);
        return value instanceof AgentRunState state ? state : null;
    }

    private static String truncate(@Nullable String text) {
        if (text == null) {
            return "";
        }
        return text.length() > AiConstants.MAX_AGENT_TOOL_CONTENT_LENGTH
            ? text.substring(0, AiConstants.MAX_AGENT_TOOL_CONTENT_LENGTH) + "…"
            : text;
    }

}
