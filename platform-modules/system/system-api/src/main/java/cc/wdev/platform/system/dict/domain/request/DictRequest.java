package cc.wdev.platform.system.dict.domain.request;

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
public class DictRequest extends PageRequest {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    private Long tenantId;
    /**
     * 业务类型
     */
    @Schema(title = "标签类型")
    private String bizType;
    /**
     * 字典编号
     */
    @Schema(title = "字典编号")
    private String code;
    /**
     * 字典ID
     */
    @Schema(title = "字典ID")
    private Long dictId;
}
