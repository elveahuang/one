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
public class AddressRequest extends PageRequest {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    private Long tenantId;
    /**
     * 业务类型
     */
    @Schema(title = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID")
    private List<Long> bizIdList;
    /**
     * 地址ID
     */
    @Schema(title = "地址ID")
    private Long addressId;
}
