package cc.wdev.platform.commons.ai.agent;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 智能体单次运行的上下文状态
 * <p>
 * 通过 advisor context 传递（见 {@link cc.wdev.platform.commons.ai.AiConstants#AGENT_RUN_STATE_CONTEXT_KEY}），
 * 在一次对话的多轮 ReAct 循环中共享：记录当前轮次、已收集的事件，并把事件分发给监听器。
 * 同一次运行内的多轮循环是串行推进的，因此原子计数器足够保证一致性。
 *
 * @author elvea
 */
public class AgentRunState {

    /**
     * 当前轮次（从 1 开始，每轮循环开始时递增）
     */
    private final AtomicInteger step = new AtomicInteger(0);

    /**
     * 已发射事件对应的工具响应消息数（避免跨轮重复发射同一批工具结果）
     */
    private final AtomicInteger emittedToolResponses = new AtomicInteger(0);

    /**
     * 已收集的全部事件（含流式与非流式模式，供运行结束后读取完整轨迹）
     */
    private final List<AgentEvent> events = new CopyOnWriteArrayList<>();

    /**
     * 事件监听器（流式模式下推送实时事件，可为 null）
     */
    @Nullable
    private final AgentEventListener listener;

    public AgentRunState() {
        this(null);
    }

    public AgentRunState(@Nullable AgentEventListener listener) {
        this.listener = listener;
    }

    /**
     * 开启新一轮循环
     */
    public void beginStep() {
        this.step.incrementAndGet();
    }

    /**
     * 当前轮次
     */
    public int currentStep() {
        return this.step.get();
    }

    /**
     * 已发射事件对应的工具响应消息数
     */
    public int getEmittedToolResponses() {
        return this.emittedToolResponses.get();
    }

    public void setEmittedToolResponses(int count) {
        this.emittedToolResponses.set(count);
    }

    /**
     * 记录并分发事件
     */
    public void emit(AgentEvent event) {
        this.events.add(event);
        if (this.listener != null) {
            this.listener.onEvent(event);
        }
    }

    public List<AgentEvent> getEvents() {
        return List.copyOf(this.events);
    }

}
