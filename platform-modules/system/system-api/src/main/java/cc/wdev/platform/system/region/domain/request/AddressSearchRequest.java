package cc.wdev.platform.system.region.domain.request;

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
public class AddressSearchRequest extends PageRequest {
    /**
     * 业务类型
     */
    @Schema(title = "标签类型", description = "标签类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID列表")
    private List<Long> bizIdList;
    /**
     * 关联业务类型
     */
    @Schema(title = "关联业务类型", description = "关联业务类型")
    private String relationBizType;
    /**
     * 关联业务ID列表
     */
    @Schema(title = "关联业务ID列表", description = "关联业务ID列表")
    private List<Long> relationBizIdList;
    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;
}
