package cc.wdev.platform.commons.oapis.weixin.service;

import cc.wdev.platform.commons.oapis.weixin.config.AppMpConfig;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;

/**
 * @author elvea
 */
public interface WxMpManager {

    WxMpConfigStorage getConfigStorage();

    WxMpConfigStorage getConfigStorage(AppMpConfig appConfig);

    WxMpService getService();

    WxMpService getService(AppMpConfig appConfig);

}
