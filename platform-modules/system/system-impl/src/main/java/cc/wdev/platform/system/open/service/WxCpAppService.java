package cc.wdev.platform.system.open.service;

import cc.wdev.platform.commons.oapis.weixin.config.AppCpConfig;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.open.domain.entity.WxCpAppEntity;
import cc.wdev.platform.system.open.domain.form.WxCpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;

/**
 * 企业微信应用 Service
 */
public interface WxCpAppService extends CachingEntityService<WxCpAppEntity, Long> {

    /**
     * 获取app公众号应用配置
     */
    AppCpConfig getAppConfig();

    /**
     * 获取企业微信应用信息
     */
    WxCpAppVo getWxCpApp();

    /**
     * 获取企业微信配置
     */
    WxCpConfigForm getWxCpConfig();

    /**
     * 保存企业微信配置
     */
    void saveWxCpConfig(WxCpConfigForm form);

}
