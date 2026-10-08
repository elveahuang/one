package cc.wdev.platform.system.core.support;

import cc.wdev.platform.commons.core.tenant.Tenant;
import cc.wdev.platform.commons.core.tenant.TenantStore;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.system.core.api.TenantApi;
import cc.wdev.platform.system.core.domain.dto.TenantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultTenantStore implements TenantStore {

    private final TenantApi tenantApi;

    /**
     * @see TenantStore#findById(Long)
     */
    @Override
    public Tenant findById(Long id) {
        log.warn("Tenant with id: {}", id);
        TenantDto tenantDto = tenantApi.findById(id);
        return getTenantByTenantDto(tenantDto);
    }

    /**
     * @see TenantStore#findByCode(String)
     */
    @Override
    public Tenant findByCode(String code) {
        log.warn("Tenant with code: {}", code);
        TenantDto tenantDto = tenantApi.findByCode(code);
        return getTenantByTenantDto(tenantDto);
    }

    /**
     * @see TenantStore#findByDomain(String)
     */
    @Override
    public Tenant findByDomain(String domain) {
        log.warn("Tenant with domain: {}", domain);
        TenantDto tenantDto = tenantApi.findByDomain(domain);
        return getTenantByTenantDto(tenantDto);
    }

    private Tenant getTenantByTenantDto(TenantDto tenantDto) {
        if (tenantDto == null) {
            log.error("Failed to find tenant");
            throw new ServiceException(ResponseCodeEnum.TENANT__NOT_PRESENT);
        }

        Tenant tenant = Tenant.createTenant(tenantDto.getId(), tenantDto.getRootInd());
        // 顶层租户不需要校验
        if (BooleanTypeEnum.getTrueValue() == tenantDto.getRootInd()) {
            return tenant;
        }

        if (Objects.equals(tenantDto.getStatus(), StatusTypeEnum.OFF.getValue())
            || Objects.equals(tenantDto.getActive(), ActiveTypeEnum.DISABLED.getValue())) {
            throw new ServiceException(ResponseCodeEnum.TENANT__NOT_ACTIVE_OR_DELETED);
        }
        if (tenantDto.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResponseCodeEnum.TENANT__EXPIRED_ERROR);
        }
        return tenant;
    }

}
