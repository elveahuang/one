package cc.wdev.platform.commons.core.cache.service;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.utils.RedissonUtils;
import cc.wdev.platform.commons.enums.RateLimitTypeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.redisson.api.RType;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.springframework.data.redis.connection.DataType;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author elvea
 * @see CacheService
 * @see RedissonCacheService
 * @see AbstractCacheService
 */
public class RedissonCacheService extends AbstractCacheService {

    private final RedissonClient redissonClient;

    private final RedissonUtils redissonUtils;

    public RedissonCacheService(RedissonClient redissonClient, RedissonUtils redissonUtils,
                                boolean cacheNullValue, int batchSize) {
        this.redissonClient = redissonClient;
        this.redissonUtils = redissonUtils;
        this.cacheNullValues = cacheNullValue;
        this.batchSize = batchSize;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 基础方法
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#exists(String)
     */
    @Override
    public Boolean exists(@NonNull String key) {
        return this.redissonClient.getBucket(key).isExists();
    }

    /**
     * @see CacheService#rename(String, String)
     */
    @Override
    public void rename(@NonNull String oldKey, @NonNull String newKey) {
        this.redissonClient.getBucket(oldKey).rename(newKey);
    }

    /**
     * @see CacheService#move(String, int)
     */
    @Override
    public Boolean move(@NonNull String key, int database) {
        return this.redissonClient.getBucket(key).move(database);
    }

    /**
     * @see CacheService#persist(String)
     */
    @Override
    public Boolean persist(@NonNull String key) {
        return this.redissonClient.getBucket(key).clearExpire();
    }

    /**
     * @see CacheService#keys(String)
     */
    @Override
    public Set<String> keys(@NonNull String pattern) {
        return Sets.newHashSet(this.redissonClient.getKeys().getKeys(KeysScanOptions.defaults().pattern(pattern).limit(10)));
    }

    /**
     * @see CacheService#scan(String)
     */
    @Override
    public Set<String> scan(@NonNull String pattern) {
        return scan(pattern, batchSize);
    }

    /**
     * @see CacheService#scan(String, int)
     */
    @Override
    public Set<String> scan(@NonNull final String pattern, final int limit) {
        return Sets.newHashSet(this.redissonClient.getKeys().getKeys(KeysScanOptions.defaults().pattern(pattern).limit(limit)));
    }

    /**
     * @see CacheService#type(String)
     */
    @Override
    public String type(@NonNull String key) {
        RType type = this.redissonClient.getKeys().getType(key);
        return type == null ? DataType.NONE.code() : type.name();
    }

    /**
     * @see CacheService#flushDb()
     */
    @Override
    public void flushDb() {
        this.redissonClient.getKeys().flushdb();
    }

    /**
     * @see CacheService#flushAll()
     */
    @Override
    public void flushAll() {
        this.redissonClient.getKeys().flushall();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 设置过期
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#expire(String, long, TimeUnit)
     */
    @Override
    public Boolean expire(@NonNull String key, long expire, TimeUnit timeUnit) {
        return redissonClient.getBucket(key).expire(Duration.ofSeconds(expire));
    }

    /**
     * @see CacheService#expireAt(String, Date)
     */
    @Override
    public Boolean expireAt(@NonNull String key, @NonNull Date date) {
        return this.redissonClient.getBucket(key).expire(date.toInstant());
    }

    /**
     * @see CacheService#getExpire(String)
     */
    @Override
    public Long getExpire(@NonNull String key) {
        long expire = redissonClient.getBucket(key).remainTimeToLive();
        if (expire > 0) {
            expire = expire / 1000;
        }
        return expire;
    }

    /**
     * @see CacheService#getExpireMill(String)
     */
    @Override
    public Long getExpireMill(@NonNull String key) {
        long expire = redissonClient.getBucket(key).getIdleTime();
        return (expire > 0) ? expire : 0L;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 删除操作
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#delete(List)
     */
    @Override
    public Long delete(@NonNull List<String> keys) {
        long total = 0;
        if (!CollectionUtils.isEmpty(keys)) {
            List<List<String>> partitionKeys = Lists.partition(keys, this.batchSize);
            for (List<String> list : partitionKeys) {
                total += this.redissonClient.getKeys().unlink(list.toArray(new String[]{}));
            }
        }
        return total;
    }

    @Override
    public void deleteByPattern(@NonNull String pattern) {
        long total = 0;
        if (StringUtils.isBlank(pattern)) {
            return;
        }
        this.redissonClient.getKeys().unlinkByPattern(pattern);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 异步删除缓存
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#unlink(List)
     */
    @Override
    public Long unlink(@Nullable List<String> keys) {
        long total = 0;
        if (!CollectionUtils.isEmpty(keys)) {
            List<List<String>> partitionKeys = Lists.partition(keys, this.batchSize);
            for (List<String> list : partitionKeys) {
                total += this.redissonClient.getKeys().delete(list.toArray(new String[]{}));
            }
        }
        return total;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 设置缓存
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#set(String, Object)
     */
    @Override
    public void set(@NonNull String key, Object value) {
        if (this.allowCacheValues(value)) {
            this.redissonClient.getBucket(key).set(isNullValue(value) ? newNullValue() : value);
        }
    }

    /**
     * @see CacheService#set(String, Object, Duration)
     */
    @Override
    public void set(@NonNull String key, Object value, Duration duration) {
        if (this.allowCacheValues(value)) {
            this.redissonClient.getBucket(key).set(isNullValue(value) ? newNullValue() : value, duration);
        }
    }

    /**
     * @see CacheService#multiSet(Map, Duration)
     */
    public void multiSet(@NonNull Map<String, Object> map) {
        Map<String, Object> mSetMap = mSetMap(map);
        mSetMap.forEach((k, v) -> this.redissonClient.getBucket(k).set(v));
    }

    /**
     * @see CacheService#multiSet(Map, Duration)
     */
    public void multiSet(@NonNull Map<String, Object> map, Duration duration) {
        Map<String, Object> mSetMap = mSetMap(map);
        mSetMap.forEach((k, v) -> this.redissonClient.getBucket(k).set(v, duration));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 获取缓存
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#get(String)
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T get(@NonNull String key) {
        T value = (T) this.redissonClient.getBucket(key).get();
        if (isCacheNullValues(value)) {
            set(key, newNullValue());
        }
        return returnVal(value);
    }

    /**
     * @see CacheService#get(CacheKey, Function)
     */
    @Nullable
    @SuppressWarnings({"unchecked"})
    public <T> T get(@NonNull CacheKey cacheKey, Function<CacheKey, T> loader) {
        T value = (T) this.redissonClient.getBucket(cacheKey.getKey()).get();
        if (isNullValue(value)) {
            Lock lock = getLock(cacheKey.getKey() + "-lock");
            try {
                lock.lock();
                value = (T) this.redissonClient.getBucket(cacheKey.getKey()).get();
                if (!isNullValue(value)) {
                    return returnVal(value);
                }
                value = loader.apply(cacheKey);
                this.set(cacheKey.getKey(), value, cacheKey.getExpire());
            } finally {
                lock.unlock();
            }
        }
        return returnVal(value);
    }

    /**
     * @see CacheService#multiGet(Collection)
     */
    public <T> List<T> multiGet(@NonNull Collection<String> keys) {
        Map<String, T> valueMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(keys)) {
            valueMap = this.redissonClient.getBuckets().get(keys.toArray(new String[]{}));
        }
        return CollectionUtils.isEmpty(valueMap) ? Collections.emptyList() : valueMap.values().stream().map(this::returnVal).collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 分布式锁
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#getLock(String)
     */
    @Override
    public Lock getLock(String key) {
        return this.redissonClient.getLock(key);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 限流
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see CacheService#getClientId()
     */
    @Override
    public String getClientId() {
        return this.redissonUtils.getClientId();
    }

    /**
     * @see CacheService#rateLimiter(String, RateLimitTypeEnum, long, long)
     */
    public long rateLimiter(String key, RateLimitTypeEnum type, long rate, long rateInterval) {
        return this.redissonUtils.rateLimiter(key, type, rate, rateInterval);
    }

}
