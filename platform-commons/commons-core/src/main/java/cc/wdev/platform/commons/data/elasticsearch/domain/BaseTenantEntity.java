package cc.wdev.platform.commons.data.elasticsearch.domain;

import cc.wdev.platform.commons.utils.NumberUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author elvea
 */
@Setter
@Getter
public abstract class BaseTenantEntity extends BaseEntity {
    /**
     * 租户ID
     */
    @Field(type = FieldType.Keyword, ignoreAbove = 256)
    protected String tenantId;

    public void setTenantId(Long tenantId) {
        this.tenantId = NumberUtils.toString(tenantId);
    }
}
