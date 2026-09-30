package cc.wdev.platform.commons.core.cache;

import cc.wdev.platform.commons.core.cache.service.CacheService;

/**
 * @author elvea
 */
public class CacheServiceManager {

    private static volatile CacheService globalCacheService;

    public static CacheService getCacheService() {
        return globalCacheService;
    }

    public static void setCacheService(CacheService cacheService) {
        globalCacheService = cacheService;
    }

}
