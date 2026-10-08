package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话查询请求")
public class AiChatPostRequest extends PageRequest {
    /**
     * 对话类型
     */
    @Schema(title = "对话类型")
    private String chatType;
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    private Long tenantId;
    /**
     * 对话ID
     */
    @Schema(title = "对话ID")
    private String conversationId;
    /**
     * 模型
     */
    @Schema(description = "模型名称")
    private String modelCode;
    /**
     * 智能体
     */
    @Schema(description = "智能体")
    private String agentCode;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 用户提示词
     */
    @Schema(description = "用户提示词")
    private String prompt;
    /**
     * 工具名称
     */
    @Schema(description = "工具名称")
    private String toolName;

    /**
     * 转换为SimpleChatRequest类型
     */
    public SimpleChatRequest toSimpleChatRequest() {
        return SimpleChatRequest
            .builder()
            .conversationId(this.conversationId)
            .userId(this.userId)
            .prompt(this.prompt)
            .build();
    }

    /**
     * 从SimpleChatRequest转换
     */
    public static AiChatPostRequest of(SimpleChatRequest request) {
        return AiChatPostRequest
            .builder()
            .conversationId(request.getConversationId())
            .userId(request.getUserId())
            .prompt(request.getPrompt())
            .modelCode(request.getModelCode())
            .build();
    }
}
