package cc.wdev.platform.system.commons.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "业务类型请求参数")
public class BizTypeRequest extends Request {

    @Schema(title = "指定租户ID", description = "指定租户ID")
    private Long tenantId;

    @Schema(title = "业务类型", description = "业务类型")
    private String type;

    @Builder.Default
    @Schema(title = "是否包含业务类型项目", description = "是否包含业务类型项目")
    private boolean withItem = false;

}
