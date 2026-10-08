package cc.wdev.platform.system.core.cache;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.TenantCacheKeyGenerator;
import cc.wdev.platform.commons.utils.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.USER_ROLE;

/**
 * @author elvea
 */
public class UserRoleCacheKeyGenerator implements TenantCacheKeyGenerator {

    @Override
    public @NotNull String getPrefix() {
        return USER_ROLE;
    }

    public static CacheKey keyByUserId(Long userId) {
        return new UserRoleCacheKeyGenerator().byUid(userId);
    }

    public static List<String> keysByUserIds(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds.stream().map(uid -> keyByUserId(uid).getKey()).toList();
    }

}
