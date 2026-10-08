package cc.wdev.platform.commons.data.mybatis.domain;

import cc.wdev.platform.commons.data.core.domain.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Setter
@Getter
public abstract class BaseTenantEntity extends BaseEntity implements TenantEntity {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long tenantId;
}
