package cc.wdev.platform.system.config.domain.request;

import cc.wdev.platform.commons.web.request.Request;
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
@SuperBuilder(builderMethodName = "customBuilder")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BizTypeSaveRequest<T> extends Request {
    @Schema(description = "业务分组类型")
    private String bizGroupType;
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "业务类型描述")
    private String description;
    @Schema(description = "附加信息")
    private String extra;
    @Schema(description = "业务配置")
    private T config;
    @Schema(description = "序号")
    private Integer idx;
    @Schema(description = "状态")
    private Integer status;
}
