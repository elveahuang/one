package cc.wdev.platform.system.open.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.open.domain.entity.WxMaAppEntity;
import cc.wdev.platform.system.open.domain.form.WxMaConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMaAppVo;

/**
 * 微信小程序应用 Service
 */
public interface WxMaAppService extends CachingEntityService<WxMaAppEntity, Long> {

    /**
     * 获取小程序配置
     */
    WxMaConfigForm getWxMaConfig();

    /**
     * 保存小程序配置
     */
    void saveWxMaConfig(WxMaConfigForm form);

    WxMaAppVo getWxMaApp();
}
