package cc.wdev.platform.commons.service;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.enums.BaseResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SpringUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用实体服务接口
 *
 * @param <T> 实体类型
 * @param <K> 主键类型
 * @author elvea
 * @see EntityService
 * @see Service
 */
public interface CachingEntityService<T extends IdEntity, K extends Serializable> extends EntityService<T, K> {

    default CacheService getCacheService() {
        return SpringUtils.getBean(CacheService.class);
    }

    default int getBatchSize() {
        return GlobalConstants.MAX_BATCH_CACHE_KEY_SIZE;
    }

    default CacheKeyGenerator getCacheKeyGenerator() {
        return SimpleCacheKeyGenerator.byClassSimpleName(this.currentEntityClass());
    }

    /**
     * 覆盖父类方法，从缓存读取
     */
    default T resolve(K id, String code, BaseResponseCodeEnum responseCode) {
        T entity = null;
        if (ObjectUtils.isValidId(id)) {
            entity = this.findCacheById(id);
        } else if (StringUtils.isNotEmpty(code)) {
            entity = this.findCacheByCode(code);
        }
        if (entity == null) {
            throw new ServiceException(responseCode);
        }
        return entity;
    }

    /**
     * 获取指定Key的缓存实体
     */
    default T findByCacheKey(CacheKey cacheKey) {
        return getCacheService().get(cacheKey);
    }

    /**
     * 获取指定Key的缓存实体
     */
    default T findByCacheKey(CacheKey cacheKey, Function<CacheKey, T> loader) {
        return getCacheService().get(cacheKey, loader);
    }

    /**
     * 获取指定Key的缓存实体
     */
    default List<T> findByCacheKey(Collection<CacheKey> keys) {
        return getCacheService().multiGetByCacheKey(keys);
    }

    /**
     * 基于缓存实现获取指定主键的实体
     *
     * @param id 主键
     * @return 实体
     */
    default T findCacheById(@Nullable K id) {
        if (ObjectUtils.isInvalidId(id)) {
            return null;
        }
        return getCacheService().get(getCacheKeyGenerator().byId(id), _ -> this.findById(id));
    }

    /**
     * 基于缓存实现获取指定主键的实体
     *
     * @param code 编号
     * @return 实体
     */
    default T findCacheByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return getCacheService().get(getCacheKeyGenerator().byCode(code), _ -> this.findByCode(code));
    }

    /**
     * 基于缓存实现批量获取指定主键的实体
     *
     * @param ids 主键
     * @return 实体集合
     */
    default List<T> findCacheByIds(Collection<K> ids) {
        return findCacheByIds(ids, k -> findByIds(ids));
    }

    /**
     * 基于缓存实现批量获取指定主键的实体
     *
     * @param ids    主键
     * @param loader 缓存不存在时回调获取实体
     * @return 实体集合
     */
    default List<T> findCacheByIds(Collection<K> ids, Function<Collection<K>, Collection<T>> loader) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 批量生成缓存键
        Map<K, CacheKey> idToCacheKey = ids.stream()
            .collect(Collectors.toMap((v) -> v, getCacheKeyGenerator()::key, (e, _) -> e));

        // 批量查询缓存
        List<CacheKey> cacheKeys = Lists.newArrayList(idToCacheKey.values());
        List<List<CacheKey>> partitions = Lists.partition(cacheKeys, getBatchSize());
        List<T> cachedValues = partitions.stream()
            .map(this::findByCacheKey)
            .flatMap(Collection::stream)
            .toList();

        // 建立缓存键到值的映射
        Map<CacheKey, T> cacheKeyToValue = Maps.newHashMapWithExpectedSize(cacheKeys.size());
        for (int i = 0; i < cacheKeys.size() && i < cachedValues.size(); i++) {
            if (cachedValues.get(i) == null) {
                continue;
            }
            cacheKeyToValue.put(cacheKeys.get(i), cachedValues.get(i));
        }

        // 分离已缓存和未缓存的ID
        List<T> result = Lists.newArrayList();
        Set<K> missingIds = Sets.newHashSet();

        for (K id : ids) {
            CacheKey cacheKey = idToCacheKey.get(id);
            T value = cacheKeyToValue.get(cacheKey);

            if (value != null) {
                result.add(value);
            } else {
                missingIds.add(id);
            }
        }

        if (CollectionUtils.isEmpty(missingIds)) {
            return result;
        }

        // 加载未缓存的数据
        if (loader == null) {
            loader = this::findByIds;
        }
        Collection<T> loaded = loader.apply(missingIds);
        loaded.forEach(entity -> {
            setCache(entity); // 缓存新数据
            result.add(entity);
        });
        return result;
    }

    /**
     * 获取指定Key的缓存实体
     */
    default List<T> findCacheByBizIds(Collection<K> bizIds,
                                      Function<K, CacheKey> converter,
                                      Function<Collection<K>, Collection<T>> loader) {
        if (CollectionUtils.isEmpty(bizIds)) {
            return Collections.emptyList();
        }

        // 批量生成缓存键
        Map<K, CacheKey> bidToCacheKey = bizIds.stream()
            .collect(Collectors.toMap((v) -> v, converter, (e, _) -> e));

        // 批量查询缓存
        List<CacheKey> cacheKeys = Lists.newArrayList(bidToCacheKey.values());
        List<List<CacheKey>> partitions = Lists.partition(cacheKeys, getBatchSize());
        List<T> cachedValues = partitions.stream()
            .map(this::findByCacheKey)
            .flatMap(Collection::stream)
            .toList();

        // 建立缓存键到值的映射
        Map<CacheKey, T> cacheKeyToValue = Maps.newHashMapWithExpectedSize(cacheKeys.size());
        for (int i = 0; i < cacheKeys.size() && i < cachedValues.size(); i++) {
            if (cachedValues.get(i) == null) {
                continue;
            }
            cacheKeyToValue.put(cacheKeys.get(i), cachedValues.get(i));
        }

        // 分离已缓存和未缓存的ID
        List<T> result = Lists.newArrayList();
        Set<K> missingBizIds = Sets.newHashSet();

        for (K bid : bizIds) {
            CacheKey cacheKey = bidToCacheKey.get(bid);
            T value = cacheKeyToValue.get(cacheKey);

            if (value != null) {
                result.add(value);
            } else {
                missingBizIds.add(bid);
            }
        }

        if (CollectionUtils.isEmpty(missingBizIds)) {
            return result;
        }

        // 加载未缓存的数据
        if (loader == null) {
            return result;
        }
        Collection<T> loaded = loader.apply(missingBizIds);
        loaded.forEach(entity -> {
            setCache(entity); // 缓存新数据
            result.add(entity);
        });
        return result;
    }

    /**
     * 刷新缓存
     */
    default void loadCache() {
        this.findAll().forEach(this::setCache);
    }

    /**
     * 清理缓存
     */
    default void clearCache() {
        this.findAll().forEach(this::deleteCache);
    }

    /**
     * 删除实体缓存
     */
    default void deleteCache(T model) {
        if (model instanceof IdEntity entity && ObjectUtils.isValidId(entity.getId())) {
            getCacheService().delete(getCacheKeyGenerator().byId(entity.getId()));
        }
        if (model instanceof CodeEntity entity && StringUtils.isNotEmpty(entity.getCode())) {
            getCacheService().delete(getCacheKeyGenerator().byCode(entity.getCode()));
        }
    }

    /**
     * 设置实体缓存
     */
    default void setCache(T model) {
        if (model instanceof IdEntity entity && ObjectUtils.isValidId(entity.getId())) {
            getCacheService().set(getCacheKeyGenerator().byId(model.getId()), entity);
        }
        if (model instanceof CodeEntity entity && StringUtils.isNotEmpty(entity.getCode())) {
            getCacheService().set(getCacheKeyGenerator().byCode(entity.getCode()), entity);
        }
    }

}
