package cc.wdev.platform.system.open.service;

import cc.wdev.platform.commons.oapis.weixin.config.AppMpConfig;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.open.domain.entity.WxMpAppEntity;
import cc.wdev.platform.system.open.domain.form.WxMpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;

/**
 * 微信公众号应用 Service
 */
public interface WxMpAppService extends CachingEntityService<WxMpAppEntity, Long> {

    /**
     * 获取app公众号应用配置
     */
    AppMpConfig getAppConfig();

    /**
     * 获取app公众号应用配置
     */
    WxMpAppVo getWxMpApp();

    /**
     * 获取微信公众号配置
     */
    WxMpConfigForm getWxMpConfig();

    /**
     * 保存微信公众号配置
     */
    void saveWxMpConfig(WxMpConfigForm form);

}
