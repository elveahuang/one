package cc.wdev.platform.system.open.service.impl;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.oapis.weixin.config.AppCpConfig;
import cc.wdev.platform.system.open.domain.converter.WxCpAppConverter;
import cc.wdev.platform.system.open.domain.entity.WxCpAppEntity;
import cc.wdev.platform.system.open.domain.form.WxCpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;
import cc.wdev.platform.system.open.repository.WxCpAppRepository;
import cc.wdev.platform.system.open.service.WxCpAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 企业微信应用 Service 实现
 */
@Slf4j
@Service
public class WxCpAppServiceImpl extends BaseEntityService<WxCpAppEntity, Long, WxCpAppRepository> implements WxCpAppService {

    /**
     * @see WxCpAppService#getAppConfig()
     */
    @Override
    public AppCpConfig getAppConfig() {
        WxCpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxCpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxCpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
        if (entity != null) {
            return AppCpConfig.builder()
                .corpId(entity.getAppId())
                .corpSecret(entity.getAppSecret())
                .token(entity.getAppToken())
                .build();
        }
        return null;
    }

    /**
     * @see WxCpAppService#getWxCpApp()
     */
    @Override
    public WxCpAppVo getWxCpApp() {
        WxCpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxCpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxCpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return WxCpAppConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see WxCpAppService#getWxCpConfig()
     */
    @Override
    public WxCpConfigForm getWxCpConfig() {
        WxCpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxCpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxCpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return WxCpAppConverter.INSTANCE.entity2Form(entity);
    }

    /**
     * @see WxCpAppService#saveWxCpConfig(WxCpConfigForm)
     */
    @Override
    public void saveWxCpConfig(WxCpConfigForm form) {
        WxCpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxCpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxCpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        if (entity == null) {
            entity = new WxCpAppEntity();
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
