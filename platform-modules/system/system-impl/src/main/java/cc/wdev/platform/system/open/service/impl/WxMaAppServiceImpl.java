package cc.wdev.platform.system.open.service.impl;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.system.open.domain.converter.WxMaAppConverter;
import cc.wdev.platform.system.open.domain.entity.WxMaAppEntity;
import cc.wdev.platform.system.open.domain.form.WxMaConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMaAppVo;
import cc.wdev.platform.system.open.repository.WxMaAppRepository;
import cc.wdev.platform.system.open.service.WxMaAppService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信小程序应用 Service 实现
 */
@Slf4j
@Service
public class WxMaAppServiceImpl extends BaseEntityService<WxMaAppEntity, Long, WxMaAppRepository> implements WxMaAppService {

    /**
     * @see WxMaAppService#getWxMaConfig()
     */
    @Override
    public WxMaConfigForm getWxMaConfig() {
        LambdaQueryChainWrapper<WxMaAppEntity> wrapper = this.lambdaQueryWrapper()
            .eq(WxMaAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMaAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        WxMaAppEntity entity = this.findOneByWrapper(wrapper);
        return WxMaAppConverter.INSTANCE.entity2Form(entity);
    }

    /**
     * @see WxMaAppService#saveWxMaConfig(WxMaConfigForm)
     */
    @Override
    public void saveWxMaConfig(WxMaConfigForm form) {
        WxMaAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxMaAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMaAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        if (entity == null) {
            entity = new WxMaAppEntity();
            entity.setTenantId(TenantContext.getTenantId());
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        entity.setAppId(form.getAppId());
        entity.setAppSecret(form.getAppSecret());
        entity.setAppAesKey(form.getAppAesKey());
        entity.setAppToken(form.getAppToken());
        entity.setTitle(form.getTitle());
        entity.setAppWxId(form.getAppWxId());
        this.save(entity);
    }

    /**
     * @see WxMaAppService#getWxMaApp()
     */
    @Override
    public WxMaAppVo getWxMaApp() {
        WxMaAppEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(WxMaAppEntity::getTenantId, TenantContext.getTenantId())
            .eq(WxMaAppEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
        return WxMaAppConverter.INSTANCE.Entity2Vo(entity);
    }

}
