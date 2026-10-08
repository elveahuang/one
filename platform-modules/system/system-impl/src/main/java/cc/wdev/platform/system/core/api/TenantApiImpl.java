package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.converter.TenantConverter;
import cc.wdev.platform.system.core.domain.dto.TenantDto;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.domain.vo.TenantVo;
import cc.wdev.platform.system.core.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author erden
 */
@Slf4j
@Service
public class TenantApiImpl implements TenantApi {

    private TenantService tenantService;

    /**
     * @see TenantApi#findById(Long)
     */
    @Override
    public TenantDto findById(Long id) {
        return TenantConverter.INSTANCE.entity2Dto(this.tenantService.findCacheById(id));
    }

    /**
     * @see TenantApi#findByCode(String)
     */
    @Override
    public TenantDto findByCode(String code) {
        return TenantConverter.INSTANCE.entity2Dto(this.tenantService.findByCode(code));
    }

    @Override
    public TenantDto findByDomain(String domain) {
        return TenantConverter.INSTANCE.entity2Dto(this.tenantService.findByDomain(domain));
    }

    @Override
    public List<TenantVo> findAll() {
        List<TenantEntity> entities = tenantService.findAll();
        return entities.stream().map(TenantConverter.INSTANCE::entity2Vo).toList();
    }

    @Override
    public long getAllTenantCount(LocalDateTime startTime, LocalDateTime endTime) {
        return tenantService.getAllTenantCount(startTime, endTime);
    }

    @Override
    public List<TenantVo> filter() {
        List<TenantEntity> tenantEntities = tenantService.filter();
        return tenantEntities.stream().map(TenantConverter.INSTANCE::entity2Vo).toList();
    }

    @Autowired
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

}
