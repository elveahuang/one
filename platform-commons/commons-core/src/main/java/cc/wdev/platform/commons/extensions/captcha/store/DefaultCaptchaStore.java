package cc.wdev.platform.commons.extensions.captcha.store;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.extensions.captcha.Captcha;

import java.time.Duration;

/**
 * @author elvea
 */
public class DefaultCaptchaStore implements CaptchaStore {

    private final CacheKeyGenerator cacheKeyGenerator;

    private final CacheService cacheService;

    public DefaultCaptchaStore(CacheService cacheService, String cacheKeyPrefix) {
        this.cacheKeyGenerator = SimpleCacheKeyGenerator.builder().prefix(cacheKeyPrefix).build();
        this.cacheService = cacheService;
    }

    @Override
    public Captcha get(String key) {
        return this.cacheService.get(this.cacheKeyGenerator.key(key));
    }

    @Override
    public void set(String key, Captcha value, Duration duration) {
        this.cacheService.set(this.cacheKeyGenerator.key(key), value, duration);
    }

    @Override
    public void remove(String key) {
        this.cacheService.expire(key, Duration.ofSeconds(0));
    }

}
