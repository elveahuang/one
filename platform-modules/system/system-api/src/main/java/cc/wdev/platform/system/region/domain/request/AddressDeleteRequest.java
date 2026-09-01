package cc.wdev.platform.system.region.domain.request;

import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
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
@Schema(description = "地址删除请求")
public class AddressDeleteRequest extends DeleteRequest {
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
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;
}
