package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "保存消息事件", description = "保存消息事件")
public class AiSessionEventSaveRequest implements Serializable {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    private Long tenantId;
    /**
     * 用户ID
     */
    @Schema(title = "用户ID")
    private Long userId;
    /**
     * 对话ID
     */
    @Schema(title = "对话ID")
    private String sessionId;
    /**
     * 业务类型
     */
    @Schema(title = "业务类型")
    private String bizType;
    /**
     * 对话类型
     */
    @Schema(title = "对话类型")
    private String chatType;
    /**
     * 模板
     */
    @Schema(title = "模板")
    private String template;
    /**
     * 数据
     */
    @Schema(title = "数据")
    private String payload;
}
