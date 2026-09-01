package cc.wdev.platform.system.core.controller.exchange;

import cc.wdev.platform.system.core.domain.converter.TenantConverter;
import cc.wdev.platform.system.core.domain.dto.TenantDto;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.service.TenantService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author erden
 */
@Slf4j
@RestController
@AllArgsConstructor
public class TenantExchangeController {

    private final TenantService tenantService;

    @GetMapping(EXCHANGE_PREFIX + "/tenant/find-by-id")
    public TenantDto findById(@Parameter(description = "ID") @RequestParam("id") Long id) {
        TenantEntity tenantEntity = tenantService.findById(id);
        if (tenantEntity == null) {
            log.info("Tenant not found with id: {}", id);
            return null;
        }
        return TenantConverter.INSTANCE.entity2Dto(tenantEntity);
    }

    @GetMapping(EXCHANGE_PREFIX + "/tenant/find-by-code")
    public TenantDto findByCode(@Parameter(description = "编码") @RequestParam("code") String code) {
        TenantEntity tenantEntity = tenantService.findByCode(code);
        if (tenantEntity == null) {
            log.info("Tenant not found with code: {}", code);
            return null;
        }
        return TenantConverter.INSTANCE.entity2Dto(tenantEntity);
    }

    @GetMapping(EXCHANGE_PREFIX + "/tenant/find-by-domain")
    public TenantDto findByDomain(@Parameter(description = "domain") @RequestParam("domain") String domain) {
        TenantEntity tenantEntity = tenantService.findByDomain(domain);
        if (tenantEntity == null) {
            log.info("Tenant not found with domain: {}", domain);
            return null;
        }
        return TenantConverter.INSTANCE.entity2Dto(tenantEntity);
    }

}
