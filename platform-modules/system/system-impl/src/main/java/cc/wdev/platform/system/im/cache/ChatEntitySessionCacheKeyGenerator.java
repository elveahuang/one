package cc.wdev.platform.system.im.cache;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.TenantCacheKeyGenerator;
import org.jetbrains.annotations.NotNull;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.CHAT_ENTITY_SESSION;

public class ChatEntitySessionCacheKeyGenerator implements TenantCacheKeyGenerator {

    @Override
    @NotNull
    public String getPrefix() {
        return CHAT_ENTITY_SESSION;
    }

    public CacheKey byEntity(Long sid, Long entityId) {
        return this.key(CHAT_ENTITY_SESSION, sid, entityId);
    }

}
