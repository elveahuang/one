package cc.wdev.platform.system.open.api;

import cc.wdev.platform.system.open.domain.form.WxMaConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMaAppVo;

/**
 * 微信小程序接口
 */
public interface WxMaApi {

    /**
     * 获取小程序配置
     */
    WxMaConfigForm getWxMaConfig();

    /**
     * 保存小程序配置
     */
    void saveWxMaConfig(WxMaConfigForm form);

    /**
     * 获取小程序应用信息，供用户端初始化用
     */
    WxMaAppVo getWxMaApp();
}
