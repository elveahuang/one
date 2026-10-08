package cc.wdev.platform.commons.data.core.domain;

/**
 * @author elvea
 */
public interface TenantEntity extends IdEntity {

    Long getTenantId();

    void setTenantId(Long tenantId);

}
