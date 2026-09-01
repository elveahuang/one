package cc.wdev.platform.commons.ai.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleChatStartRequest extends Request {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 智能体ID
     */
    @Schema(description = "智能体ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long agentId;
    /**
     * 智能体标识
     */
    @Schema(description = "智能体标识")
    private String agentCode;
}
