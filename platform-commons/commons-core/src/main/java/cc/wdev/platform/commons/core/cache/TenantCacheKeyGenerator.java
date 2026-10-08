package cc.wdev.platform.commons.core.cache;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import org.springframework.util.StringUtils;

public interface TenantCacheKeyGenerator extends CacheKeyGenerator {

    default String cacheKey(Object... params) {
        return String.format("%s:%s:%s",
            this.getPrefix(),
            TenantContext.getTenantId(),
            StringUtils.arrayToDelimitedString(params, "_")
        ).toLowerCase();
    }

    default CacheKey key(Object... params) {
        return new CacheKey(cacheKey(params), getExpire());
    }

    default String keyByPattern(Long tenantId) {
        return String.format("%s:%s:**", getPrefix(), tenantId);
    }

}
