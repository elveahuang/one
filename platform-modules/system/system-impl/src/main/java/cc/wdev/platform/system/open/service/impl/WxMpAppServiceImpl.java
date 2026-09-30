package cc.wdev.platform.system.open.service.impl;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.oapis.weixin.config.AppMpConfig;
import cc.wdev.platform.system.open.domain.converter.WxMpAppConverter;
import cc.wdev.platform.system.open.domain.entity.WxMpAppEntity;
import cc.wdev.platform.system.open.domain.form.WxMpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;
import cc.wdev.platform.system.open.repository.WxMpAppRepository;
import cc.wdev.platform.system.open.service.WxMpAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信公众号应用 Service 实现
 */
@Slf4j
@Service
public class WxMpAppServiceImpl extends BaseEntityService<WxMpAppEntity, Long, WxMpAppRepository> implements WxMpAppService {

    /**
     * @see WxMpAppService#getAppConfig()
     */
    @Override
    public AppMpConfig getAppConfig() {
        WxMpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxMpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
        if (entity != null) {
            return AppMpConfig.builder()
                .appId(entity.getAppId())
                .appSecret(entity.getAppSecret())
                .token(entity.getAppToken())
                .aesKey(entity.getAppToken())
                .build();
        }
        return null;
    }

    /**
     * @see WxMpAppService#getWxMpApp()
     */
    @Override
    public WxMpAppVo getWxMpApp() {
        WxMpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxMpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return WxMpAppConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see WxMpAppService#getWxMpConfig()
     */
    @Override
    public WxMpConfigForm getWxMpConfig() {
        WxMpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxMpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return WxMpAppConverter.INSTANCE.entity2Form(entity);
    }

    /**
     * @see WxMpAppService#saveWxMpConfig(WxMpConfigForm)
     */
    @Override
    public void saveWxMpConfig(WxMpConfigForm form) {
        WxMpAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxMpAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMpAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        if (entity == null) {
            entity = new WxMpAppEntity();
            entity.setTenantId(TenantContext.getTenantId());
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        entity.setAppId(form.getAppId());
        entity.setAppSecret(form.getAppSecret());
        entity.setAppAesKey(form.getAppAesKey());
        entity.setAppToken(form.getAppToken());
        entity.setTitle(form.getTitle());
        this.save(entity);
    }

}
