package cc.wdev.platform.system.open.api;

import cc.wdev.platform.commons.oapis.weixin.config.AppCpConfig;
import cc.wdev.platform.system.open.domain.form.WxCpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;
import me.chanjar.weixin.cp.api.WxCpService;

/**
 * 企业微信接口
 */
public interface WxCpApi {

    /**
     * 获取企业微信应用信息
     */
    WxCpService getService();

    /**
     * 获取企业微信应用信息
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
