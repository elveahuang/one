package cc.wdev.platform.system.open.service.impl;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.system.open.domain.converter.DingtalkAppConverter;
import cc.wdev.platform.system.open.domain.entity.DingtalkAppEntity;
import cc.wdev.platform.system.open.domain.form.DingtalkConfigForm;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;
import cc.wdev.platform.system.open.repository.DingtalkAppRepository;
import cc.wdev.platform.system.open.service.DingtalkAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 钉钉应用 Service 实现
 */
@Slf4j
@Service
public class DingtalkAppServiceImpl extends BaseEntityService<DingtalkAppEntity, Long, DingtalkAppRepository> implements DingtalkAppService {

    /**
     * @see DingtalkAppService#getDingtalkApp()
     */
    @Override
    public DingtalkAppVo getDingtalkApp() {
        DingtalkAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(DingtalkAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(DingtalkAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return DingtalkAppConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see DingtalkAppService#getDingtalkConfig()
     */
    @Override
    public DingtalkConfigForm getDingtalkConfig() {
        DingtalkAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(DingtalkAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(DingtalkAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return DingtalkAppConverter.INSTANCE.entity2Form(entity);
    }

    /**
     * @see DingtalkAppService#saveDingtalkConfig(DingtalkConfigForm)
     */
    @Override
    public void saveDingtalkConfig(DingtalkConfigForm form) {
        DingtalkAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(DingtalkAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(DingtalkAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        if (entity == null) {
            entity = new DingtalkAppEntity();
            entity.setTenantId(TenantContext.getTenantId());
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        entity.setAppId(form.getAppId());
        entity.setAppSecret(form.getAppSecret());
        entity.setAppAesKey(form.getAppAesKey());
        entity.setAppToken(form.getAppToken());
        entity.setTitle(form.getTitle());
        entity.setDescription(form.getDescription());
        this.save(entity);
    }

}
