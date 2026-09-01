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
@Schema(description = "智能体查询请求")
public class AiAgentSearchRequest extends PageRequest {
    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
}
