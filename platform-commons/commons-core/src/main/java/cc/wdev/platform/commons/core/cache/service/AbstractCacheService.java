package cc.wdev.platform.commons.core.cache.service;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.cache.NullValue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author elvea
 * @see CacheService
 */
public abstract class AbstractCacheService implements CacheService {

    protected boolean cacheNullValues = true;

    protected int batchSize = GlobalConstants.MAX_BATCH_CACHE_KEY_SIZE;

    // -----------------------------------------------------------------------------------------------------------------
    // 基础支持方法
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 是否空对象
     *
     * @return 是否空对象
     */
    protected <T> boolean isNullValue(@Nullable T obj) {
        return obj == null || NullValue.class.equals(obj.getClass());
    }

    /**
     * 创建空对象
     *
     * @return 空对象
     */
    protected NullValue newNullValue() {
        return new NullValue();
    }

    /**
     * 返回正常对象值得
     * 如果对象是空对象，则返回null的普通类型
     *
     * @param value 对象
     * @param <T>   对象类型
     * @return 正常的对象
     */
    protected <T> T returnVal(T value) {
        return isNullValue(value) ? null : value;
    }

    /**
     * 是否缓存空值
     */
    protected boolean isCacheNullValues() {
        return this.cacheNullValues;
    }

    /**
     * 是否缓存空值
     */
    protected boolean isCacheNullValues(Object value) {
        return ObjectUtils.isEmpty(value) && isCacheNullValues();
    }

    /**
     * 是否允许缓存
     */
    protected boolean allowCacheValues(Object value) {
        return !ObjectUtils.isEmpty(value) || (ObjectUtils.isEmpty(value) && isCacheNullValues());
    }

    protected Map<String, Object> mSetMap(@NonNull Map<String, Object> map) {
        Map<String, Object> mSetMap = new HashMap<>();
        map.forEach((k, v) -> {
            if (this.allowCacheValues(v)) {
                mSetMap.put(k, (isCacheNullValues(v)) ? newNullValue() : v);
            }
        });
        return mSetMap;
    }

}
