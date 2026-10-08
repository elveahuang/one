package cc.wdev.platform.system.dict.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典查询请求")
public class DictSearchRequest extends PageRequest {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;
    /**
     * 业务类型
     */
    @Schema(title = "标签类型", description = "标签类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    private Long bizId;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID列表", description = "业务ID列表")
    private List<Long> bizIdList;
}
