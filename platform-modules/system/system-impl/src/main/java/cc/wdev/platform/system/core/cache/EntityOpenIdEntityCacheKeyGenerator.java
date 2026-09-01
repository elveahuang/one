package cc.wdev.platform.system.core.cache;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.TenantCacheKeyGenerator;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import org.jetbrains.annotations.NotNull;

public class EntityOpenIdEntityCacheKeyGenerator implements TenantCacheKeyGenerator {

    @Override
    public @NotNull String getPrefix() {
        return SystemCacheConstants.ENTITY_OPEN_ID;
    }

    public CacheKey keyByOpenId(String bizType, String openId) {
        return this.key("open", bizType, openId);
    }

    public CacheKey keyByBizId(String bizType, Long bizId) {
        return this.key("biz", bizType, bizId);
    }

}
