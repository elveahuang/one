package cc.wdev.platform.commons.data.jpa.domain;

import cc.wdev.platform.commons.data.core.domain.TenantEntity;
import cc.wdev.platform.commons.data.jpa.tenant.TenantId;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SimpleTenantEntity extends SimpleEntity implements TenantEntity {
    /**
     * 租户ID
     */
    @TenantId
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long tenantId;
}
