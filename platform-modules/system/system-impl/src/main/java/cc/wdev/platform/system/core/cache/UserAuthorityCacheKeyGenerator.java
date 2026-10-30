package cc.wdev.platform.system.core.cache;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.TenantCacheKeyGenerator;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.USER_AUTHORITY;

/**
 * @author elvea
 */
public class UserAuthorityCacheKeyGenerator implements TenantCacheKeyGenerator {

    public static final String USER_ID_KEY = "userid";

    public static String keyPattern(Long tenantId) {
        return String.format("%s:%s:%s_**", USER_AUTHORITY, tenantId, USER_ID_KEY.toLowerCase());
    }

    @NotNull
    @Override
    public String getPrefix() {
        return USER_AUTHORITY;
    }

    public static CacheKey keyByUserId(Long userId) {
        return new UserAuthorityCacheKeyGenerator().byUid(userId);
    }

    /**
     * 切换身份后权限集合不同，缓存键必须区分角色类型，否则会命中切换前的旧权限。
     * roleType 为空时与 {@link #keyByUserId(Long)} 完全一致（保持向后兼容）。
     */
    public static CacheKey keyByUserId(Long userId, String roleType) {
        CacheKey base = keyByUserId(userId);
        if (StringUtils.isEmpty(roleType)) {
            return base;
        }
        return new CacheKey(base.getKey() + ":" + roleType.toLowerCase());
    }

    public static List<String> keysByUserIds(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds.stream().map(uid -> keyByUserId(uid).getKey()).toList();
    }

}
