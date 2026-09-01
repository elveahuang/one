package cc.wdev.platform.system.core.cache;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.TenantCacheKeyGenerator;
import cc.wdev.platform.commons.utils.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.ENTITY_AUTHORITY;

public class EntityAuthorityCacheKeyGenerator implements TenantCacheKeyGenerator {

    @Override
    @NotNull
    public String getPrefix() {
        return ENTITY_AUTHORITY;
    }

    public CacheKey byEntity(String bizType, Long bizId) {
        return this.key(bizType, bizId);
    }

    public static CacheKey keyByEntity(String bizType, Long bizId) {
        return new EntityAuthorityCacheKeyGenerator().byEntity(bizType, bizId);
    }

    public static List<String> keysByBizIds(String bizType, Collection<Long> bizIds) {
        if (CollectionUtils.isEmpty(bizIds)) {
            return Collections.emptyList();
        }
        CacheKeyGenerator generator = new EntityAuthorityCacheKeyGenerator();
        return bizIds.stream().map(bizId -> generator.cacheKey(bizType, bizId)).toList();
    }

}
