package cc.wdev.platform.commons.oapis.weixin.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.oapis.weixin.config.AppCpConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxCpManager;
import cc.wdev.platform.commons.oapis.weixin.storage.WxCpCacheConfigStorage;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.cp.api.WxCpMessageService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.WxCpUserService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;

/**
 * @author elvea
 */
@Getter
@Setter
public class WxCpManagerImpl implements WxCpManager {

    private final CacheService cacheService;

    private final String cacheKeyPrefix;

    private AppCpConfig appConfig;

    public WxCpManagerImpl(CacheService cacheService, String cacheKeyPrefix) {
        this.cacheService = cacheService;
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    /**
     * @see WxCpManager#getConfigStorage()
     */
    @Override
    public WxCpConfigStorage getConfigStorage() {
        return this.getConfigStorage(this.appConfig);
    }

    /**
     * @see WxCpManager#getConfigStorage(AppCpConfig)
     */
    @Override
    public WxCpConfigStorage getConfigStorage(AppCpConfig appCpConfig) {
        WxCpCacheConfigStorage config = new WxCpCacheConfigStorage(cacheService, cacheKeyPrefix);
        config.setCorpId(appCpConfig.getCorpId());
        config.setCorpSecret(appCpConfig.getCorpSecret());
        config.setAgentId(appCpConfig.getAgentId());
        if (StringUtils.isNotEmpty(appCpConfig.getToken())) {
            config.setToken(appCpConfig.getToken());
        }
        return config;
    }

    /**
     * @see WxCpManager#getService()
     */
    @Override
    public WxCpService getService() {
        return this.getService(this.appConfig);
    }

    /**
     * @see WxCpManager#getService(AppCpConfig)
     */
    @Override
    public WxCpService getService(AppCpConfig appCpConfig) {
        WxCpService wxCpService = new WxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(getConfigStorage(appCpConfig));
        return wxCpService;
    }

    /**
     * @see WxCpManager#getMessageService(AppCpConfig)
     */
    @Override
    public WxCpMessageService getMessageService() {
        return this.getMessageService(this.getAppConfig());
    }

    /**
     * @see WxCpManager#getMessageService(AppCpConfig)
     */
    @Override
    public WxCpMessageService getMessageService(AppCpConfig appConfig) {
        return this.getService(appConfig).getMessageService();
    }

    /**
     * @see WxCpManager#getUserService(AppCpConfig)
     */
    @Override
    public WxCpUserService getUserService() {
        return this.getUserService(this.getAppConfig());
    }

    /**
     * @see WxCpManager#getUserService(AppCpConfig)
     */
    @Override
    public WxCpUserService getUserService(AppCpConfig appConfig) {
        return this.getService(appConfig).getUserService();
    }

}
