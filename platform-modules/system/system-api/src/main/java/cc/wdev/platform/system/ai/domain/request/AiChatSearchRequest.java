package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话查询请求")
public class AiChatSearchRequest extends PageRequest {
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
     * 对话类型
     */
    @Schema(title = "对话类型")
    private String chatType;
    /**
     * 智能体编号
     */
    @Schema(title = "智能体编号")
    private String agentCode;

    /**
     * 游标号
     */
    @Schema(title = "游标号")
    private Long lastId;
}
