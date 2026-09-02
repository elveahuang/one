package cc.wdev.platform.commons.ai.agent;

/**
 * 智能体事件监听器
 * <p>
 * 在 ReAct 循环推进过程中接收 {@link AgentEvent}，用于推送 SSE 事件流或记录执行轨迹。
 *
 * @author elvea
 */
@FunctionalInterface
public interface AgentEventListener {

    void onEvent(AgentEvent event);

}
