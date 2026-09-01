package cc.wdev.platform.commons.core.cache;

import cc.wdev.platform.commons.constants.GlobalConstants;
import org.jspecify.annotations.NonNull;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author elvea
 */
public interface CacheKeyGenerator {

    String PREFIX_ID = "id";

    String PREFIX_UUID = "uuid";

    String PREFIX_CODE = "code";

    String PREFIX_USER_ID = "uid";

    String PREFIX_OPEN_ID = "oid";

    @NonNull
    String getPrefix();

    @NonNull
    default Duration getExpire() {
        return GlobalConstants.DEFAULT_CACHE_DURATION;
    }

    default String cacheKey(Object... params) {
        return String.format("%s:%s", this.getPrefix(),
            StringUtils.arrayToDelimitedString(params, "_")
        ).toLowerCase();
    }

    default CacheKey key(Object... params) {
        return new CacheKey(cacheKey(params), getExpire());
    }

    default CacheKey byId(Serializable id) {
        return this.key(PREFIX_ID, id);
    }

    default CacheKey byCode(String code) {
        return this.key(PREFIX_CODE, code);
    }

    default CacheKey byUuid(String uuid) {
        return this.key(PREFIX_UUID, uuid);
    }

    default CacheKey byUid(Long userId) {
        return this.key(PREFIX_USER_ID, userId);
    }

    default CacheKey byOid(Long userId) {
        return this.key(PREFIX_OPEN_ID, userId);
    }

    default CacheKey byBizType(String bizType, Long bizId) {
        return this.key(bizType, bizId);
    }

    default CacheKey byBizType(String bizType, String bizId) {
        return this.key(bizType, bizId);
    }

    default CacheKey byBizTypeCode(String bizType, String code) {
        return this.key(bizType, code);
    }

}
