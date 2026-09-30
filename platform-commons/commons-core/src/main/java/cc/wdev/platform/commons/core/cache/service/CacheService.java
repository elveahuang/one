package cc.wdev.platform.commons.core.cache.service;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.enums.RateLimitTypeEnum;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
public interface CacheService {

    // -----------------------------------------------------------------------------------------------------------------
    // 基础方法
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 检查key是否存在
     */
    Boolean exists(@NonNull String key);

    /**
     * 重命名
     */
    void rename(@NonNull String oldKey, @NonNull String newKey);

    /**
     * 移动到指定到数据库中
     */
    Boolean move(@NonNull String key, int database);

    /**
     * 持久化
     */
    Boolean persist(@NonNull String key);

    /**
     * 返回指定模式的key
     * 生产环境一般禁用keys命令，所以这个方法不建议使用，
     */
    Set<String> keys(@NonNull String pattern);

    /**
     * 返回指定模式的key
     */
    Set<String> scan(@NonNull final String pattern);

    /**
     * 返回指定模式的key
     */
    Set<String> scan(@NonNull final String pattern, final int limit);

    /**
     * 返回类型
     */
    String type(@NonNull String key);

    /**
     * 清空缓存
     */
    void flushDb();

    /**
     * 清空所有缓存
     */
    void flushAll();

    // -----------------------------------------------------------------------------------------------------------------
    // 设置过期和返回过期时间
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 设定过期时间
     */
    default Boolean expire(@NonNull String key, long seconds) {
        return this.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 设定过期时间
     */
    default Boolean expire(@NonNull String key, @NonNull Duration timeout) {
        return this.expire(key, timeout.getSeconds());
    }

    /**
     * 设定过期时间
     */
    Boolean expire(@NonNull String key, long expire, TimeUnit timeUnit);

    /**
     * 设定过期时间
     */
    Boolean expireAt(@NonNull String key, @NonNull Date date);

    /**
     * 设定过期时间
     */
    default Boolean expireAt(@NonNull String key, long unixTimestamp) {
        return expireAt(key, new Date(unixTimestamp));
    }

    /**
     * 返回过期时间
     */
    Long getExpire(@NonNull String key);

    /**
     * 返回过期时间
     */
    Long getExpireMill(@NonNull String key);

    // -----------------------------------------------------------------------------------------------------------------
    // 删除缓存
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 删除指定缓存
     */
    default Long delete(@NonNull CacheKey... keys) {
        return this.delete(Arrays.stream(keys).map(CacheKey::getKey).collect(Collectors.toList()));
    }

    /**
     * 删除指定缓存
     */
    default Long delete(@NonNull String... keys) {
        return delete(Arrays.stream(keys).collect(Collectors.toList()));
    }

    /**
     * 删除指定缓存
     */
    default Long delete(@NonNull Collection<CacheKey> keys) {
        return delete(keys.stream().map(CacheKey::getKey).collect(Collectors.toList()));
    }

    /**
     * 删除指定缓存
     */
    Long delete(@NonNull List<String> keys);

    /**
     * 删除匹配的缓存
     */
    void deleteByPattern(@NonNull String pattern);

    // -----------------------------------------------------------------------------------------------------------------
    // 异步删除缓存
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 异步删除指定的缓存
     */
    default Long unlink(@NonNull CacheKey... keys) {
        return unlink(Arrays.stream(keys).map(CacheKey::getKey).collect(Collectors.toList()));
    }

    /**
     * 异步删除指定的缓存
     */
    default Long unlink(@NonNull String... keys) {
        return unlink(Arrays.stream(keys).collect(Collectors.toList()));
    }

    /**
     * 异步删除指定的缓存
     */
    Long unlink(@NonNull List<String> keys);

    // -----------------------------------------------------------------------------------------------------------------
    // 基础支持方法
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 设置缓存
     */
    default void set(@NonNull CacheKey cacheKey, Object value) {
        Duration expire = cacheKey.getExpire();
        if (expire == null) {
            set(cacheKey.getKey(), value);
        } else {
            set(cacheKey.getKey(), value, expire);
        }
    }

    /**
     * 设置缓存
     */
    default void set(@NonNull CacheKey cacheKey, Object value, Duration duration) {
        set(cacheKey.getKey(), value, duration);
    }

    /**
     * 设置缓存
     */
    void set(@NonNull String key, Object value);

    /**
     * 设置缓存
     */
    void set(@NonNull String key, Object value, Duration duration);

    /**
     * 批量设置缓存
     */
    void multiSet(@NonNull Map<String, Object> map);

    /**
     * 批量设置缓存
     */
    void multiSet(@NonNull Map<String, Object> map, Duration duration);

    // -----------------------------------------------------------------------------------------------------------------
    // 基础支持方法
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 获取缓存
     */
    <T> T get(@NonNull String key);

    /**
     * 获取缓存
     */
    default <T> T get(@NonNull CacheKey cacheKey) {
        return this.get(cacheKey.getKey());
    }

    /**
     * 获取缓存
     */
    <T> T get(@NonNull CacheKey cacheKey, Function<CacheKey, T> loader);

    /**
     * 批量获取缓存
     */
    <T> List<T> multiGet(@NonNull Collection<String> keys);

    /**
     * 批量获取缓存
     */
    default <T> List<T> multiGetByCacheKey(@NonNull Collection<CacheKey> cacheKeys) {
        List<String> keys = cacheKeys.stream().map(CacheKey::getKey).collect(Collectors.toList());
        return this.multiGet(keys);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 分布式锁
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 获取锁
     */
    Lock getLock(String key);

    // -----------------------------------------------------------------------------------------------------------------
    // 限流
    // -----------------------------------------------------------------------------------------------------------------

    default String getClientId() {
        return "redis";
    }

    long rateLimiter(String key, RateLimitTypeEnum type, long rate, long rateInterval);

}
