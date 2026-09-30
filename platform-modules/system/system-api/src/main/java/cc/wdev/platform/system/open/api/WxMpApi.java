package cc.wdev.platform.system.open.api;

import cc.wdev.platform.commons.oapis.weixin.config.AppMpConfig;
import cc.wdev.platform.system.open.domain.form.WxMpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 微信公众号接口
 */
public interface WxMpApi {

    /**
     * 获取微信公众号基础服务
     */
    WxMpService getService();

    /**
     * 获取微信公众号基础配置
     */
    AppMpConfig getAppConfig();

    /**
     * 获取公众号应用信息，供用户端初始化用
     */
    WxMpAppVo getWxMpApp();

    /**
     * 获取微信公众号配置，供管理端编辑配置用
     */
    WxMpConfigForm getWxMpConfig();

    /**
     * 保存微信公众号配置
     */
    void saveWxMpConfig(WxMpConfigForm form);

}
