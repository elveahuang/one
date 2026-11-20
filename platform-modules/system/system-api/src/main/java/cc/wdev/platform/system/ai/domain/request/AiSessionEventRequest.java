package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiSessionEventRequest extends PageRequest {
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
     * 对话ID
     */
    @Schema(title = "对话ID")
    private String conversationId;
    /**
     * Session ID
     */
    @Schema(description = "Session ID")
    private String sessionId;
    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;
}
