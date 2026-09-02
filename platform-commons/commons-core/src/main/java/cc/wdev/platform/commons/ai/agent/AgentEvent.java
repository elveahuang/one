package cc.wdev.platform.commons.ai.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 智能体执行过程事件
 * <p>
 * 描述 ReAct 循环中的一步：思考（Thought）、工具调用（Action）或工具结果（Observation），
 * 通过 SSE 事件流推送给前端展示智能体的执行轨迹。
 *
 * @author elvea
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "智能体执行过程事件")
public class AgentEvent implements Serializable {

    /**
     * 事件类型，取值见 {@link cc.wdev.platform.commons.ai.enums.AiContentType}
     */
    @Schema(name = "类型", description = "事件类型：thought/tool_call/tool_result")
    private String type;

    /**
     * 轮次（从 1 开始）
     */
    @Schema(name = "轮次", description = "轮次（从 1 开始）")
    private Integer step;

    /**
     * 工具名称（type=tool_call/tool_result 时携带）
     */
    @Schema(name = "工具名称", description = "工具名称")
    private String toolName;

    /**
     * 工具入参（type=tool_call 时携带）
     */
    @Schema(name = "工具入参", description = "工具入参（JSON 字符串）")
    private String toolArgs;

    /**
     * 工具结果（type=tool_result 时携带）
     */
    @Schema(name = "工具结果", description = "工具结果")
    private String toolResult;

    /**
     * 思考内容（type=thought 时携带）
     */
    @Schema(name = "思考内容", description = "思考内容")
    private String content;

    public static AgentEvent thought(int step, String content) {
        return AgentEvent.builder()
            .type(cc.wdev.platform.commons.ai.enums.AiContentType.THOUGHT.getValue())
            .step(step)
            .content(content)
            .build();
    }

    public static AgentEvent toolCall(int step, String toolName, String toolArgs) {
        return AgentEvent.builder()
            .type(cc.wdev.platform.commons.ai.enums.AiContentType.TOOL_CALL.getValue())
            .step(step)
            .toolName(toolName)
            .toolArgs(toolArgs)
            .build();
    }

    public static AgentEvent toolResult(int step, String toolName, String toolResult) {
        return AgentEvent.builder()
            .type(cc.wdev.platform.commons.ai.enums.AiContentType.TOOL_RESULT.getValue())
            .step(step)
            .toolName(toolName)
            .toolResult(toolResult)
            .build();
    }

}
