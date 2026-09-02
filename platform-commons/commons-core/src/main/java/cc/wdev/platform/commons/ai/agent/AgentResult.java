package cc.wdev.platform.commons.ai.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 智能体执行结果
 *
 * @author elvea
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "智能体执行结果")
public class AgentResult implements Serializable {

    /**
     * 最终回答内容
     */
    @Schema(name = "回答内容", description = "最终回答内容")
    private String content;

    /**
     * 执行过程事件（思考/工具调用/工具结果）
     */
    @Schema(name = "执行事件", description = "执行过程事件")
    private List<AgentEvent> events;

}
