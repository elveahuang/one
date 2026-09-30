package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "对话删除请求")
public class AiChatDeleteRequest extends Request {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    private Long tenantId;
    /**
     * 对话ID列表
     */
    @Schema(description = "对话ID列表")
    @JsonProperty("ids")
    private String[] ids;
}
