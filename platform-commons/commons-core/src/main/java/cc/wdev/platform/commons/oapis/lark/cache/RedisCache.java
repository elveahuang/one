package cc.wdev.platform.commons.oapis.lark.cache;

import cc.wdev.platform.commons.core.cache.service.CacheService;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 */
public record RedisCache(CacheService cacheService, String keyPrefix) implements Cache {

    @Override
    public String get(String key) {
        return this.cacheService.get(key);
    }

    @Override
    public void set(String key, String value, int expire, TimeUnit timeUnit) {
        this.cacheService.set(key, value, Duration.ofSeconds(expire));
    }

}
