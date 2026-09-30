package cc.wdev.platform.commons.oapis.weixin.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.weixin.config.AppMpConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxMpManager;
import cc.wdev.platform.commons.oapis.weixin.storage.WxMpCacheConfigStorage;
import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;

/**
 * @author elvea
 */
@Getter
@Setter
public class WxMpManagerImpl implements WxMpManager {

    private CacheService cacheService;

    private final String cacheKeyPrefix;

    private AppMpConfig appConfig;

    public WxMpManagerImpl(CacheService cacheService, String cacheKeyPrefix) {
        this.cacheService = cacheService;
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    /**
     * @see WxMpManager#getConfigStorage()
     */
    @Override
    public WxMpConfigStorage getConfigStorage() {
        return this.getConfigStorage(this.appConfig);
    }

    /**
     * @see WxMpManager#getConfigStorage(AppMpConfig)
     */
    @Override
    public WxMpConfigStorage getConfigStorage(AppMpConfig appMpConfig) {
        WxMpCacheConfigStorage config = new WxMpCacheConfigStorage(cacheService, cacheKeyPrefix);
        config.setAppId(appMpConfig.getAppId());
        config.setSecret(appMpConfig.getAppSecret());
        config.setToken(appMpConfig.getToken());
        config.setAesKey(appMpConfig.getAesKey());
        return config;
    }

    /**
     * @see WxMpManager#getService()
     */
    @Override
    public WxMpService getService() {
        return this.getService(this.appConfig);
    }

    /**
     * @see WxMpManager#getService(AppMpConfig)
     */
    @Override
    public WxMpService getService(AppMpConfig appMpConfig) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(getConfigStorage(appMpConfig));
        return wxMpService;
    }

}
