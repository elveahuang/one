package cc.wdev.platform.system.open.service.impl;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.system.open.domain.converter.LarkAppConverter;
import cc.wdev.platform.system.open.domain.entity.LarkAppEntity;
import cc.wdev.platform.system.open.domain.form.LarkConfigForm;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;
import cc.wdev.platform.system.open.repository.LarkAppRepository;
import cc.wdev.platform.system.open.service.LarkAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 飞书应用 Service 实现
 */
@Slf4j
@Service
public class LarkAppServiceImpl extends BaseEntityService<LarkAppEntity, Long, LarkAppRepository> implements LarkAppService {

    /**
     * @see LarkAppService#getLarkApp()
     */
    @Override
    public LarkAppVo getLarkApp() {
        LarkAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(LarkAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(LarkAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return LarkAppConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see LarkAppService#getLarkConfig()
     */
    @Override
    public LarkConfigForm getLarkConfig() {
        LarkAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(LarkAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(LarkAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return LarkAppConverter.INSTANCE.entity2Form(entity);
    }

    /**
     * @see LarkAppService#saveLarkConfig(LarkConfigForm)
     */
    @Override
    public void saveLarkConfig(LarkConfigForm form) {
        LarkAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(LarkAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(LarkAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        if (entity == null) {
            entity = new LarkAppEntity();
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
