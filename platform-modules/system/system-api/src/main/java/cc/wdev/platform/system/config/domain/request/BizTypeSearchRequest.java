package cc.wdev.platform.system.config.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author irving
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "业务类型查询请求")
public class BizTypeSearchRequest extends PageRequest {
    /**
     * 业务组类型
     */
    @Schema(title = "业务组类型", description = "业务组类型")
    private String bizGroupType;
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
}
