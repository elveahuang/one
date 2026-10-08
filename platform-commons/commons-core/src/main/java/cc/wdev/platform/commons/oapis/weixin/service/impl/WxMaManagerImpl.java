package cc.wdev.platform.commons.oapis.weixin.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.weixin.config.AppMaConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxMaManager;
import cc.wdev.platform.commons.oapis.weixin.storage.WxMaCacheConfigStorage;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author elvea
 */
@Setter
@Getter
public class WxMaManagerImpl implements WxMaManager {

    private final CacheService cacheService;

    private final String cacheKeyPrefix;

    private AppMaConfig appConfig;

    public WxMaManagerImpl(CacheService cacheService, String cacheKeyPrefix) {
        this.cacheService = cacheService;
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    /**
     * @see WxMaManager#getConfigStorage()
     */
    @Override
    public WxMaConfig getConfigStorage() {
        return this.getConfigStorage(this.appConfig);
    }

    /**
     * @see WxMaManager#getConfigStorage(AppMaConfig)
     */
    @Override
    public WxMaConfig getConfigStorage(AppMaConfig appMaConfig) {
        WxMaCacheConfigStorage config = new WxMaCacheConfigStorage(cacheService, cacheKeyPrefix);
        config.setAppid(appMaConfig.getAppId());
        config.setSecret(appMaConfig.getAppSecret());
        return config;
    }

    /**
     * @see WxMaManager#getService()
     */
    @Override
    public WxMaService getService() {
        return this.getService(this.appConfig);
    }

    /**
     * @see WxMaManager#getService(AppMaConfig)
     */
    @Override
    public WxMaService getService(AppMaConfig appMaConfig) {
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(getConfigStorage(appMaConfig));
        return wxMaService;
    }

}
