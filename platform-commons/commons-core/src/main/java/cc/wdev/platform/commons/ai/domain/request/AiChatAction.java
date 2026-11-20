package cc.wdev.platform.commons.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 交互卡片动作
 *
 * <p>开场白/UI 组件上的按钮点击后回传，用于按需选择对话能力：
 * {@code type} 为 agent/subAgent/tool/skill，{@code code} 为对应能力编码。</p>
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "交互卡片动作")
public class AiChatAction implements Serializable {

    /**
     * 动作幂等键（前端生成，重试复用同一 id）
     */
    @Schema(description = "动作幂等键")
    private String id;

    /**
     * 点击行为：select/send/select_send_text/select_set_text
     */
    @Schema(description = "点击行为")
    private String action;

    /**
     * 能力类型：agent/subAgent/tool/skill
     */
    @Schema(description = "能力类型")
    private String type;

    /**
     * 能力编码：agentCode / toolName / skillCode / subAgentCode
     */
    @Schema(description = "能力编码")
    private String code;

    /**
     * 前端状态：pending/sent/failed（仅埋点与排查）
     */
    @Schema(description = "前端状态")
    private String state;

}
