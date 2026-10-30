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
     * 匹配该用户**所有身份端维度**权限缓存的模式。
     *
     * <p>权限缓存键在基础键后带身份端后缀（见 {@link #keyByUserId(Long, String)}），
     * 因此角色/关联变更时只删基础键清不掉 {@code :admin}/{@code :member} 等变体，
     * 旧权限会一直命中到 TTL 结束。按模式删除可以覆盖当前与将来新增的所有端后缀。</p>
     */
    public static String keyPatternByUserId(Long userId) {
        return keyByUserId(userId).getKey() + "*";
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

    /**
     * 匹配这批用户所有身份端维度权限缓存的模式，用于按模式删除。
     *
     * <p>{@link #keysByUserIds(Collection)} 只给出不带端后缀的基础键，无法清掉
     * {@code :admin}/{@code :member} 变体；清理权限缓存时应当用它配合
     * {@code CacheService#deleteByPattern} 使用。</p>
     */
    public static List<String> keyPatternsByUserIds(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds.stream().map(UserAuthorityCacheKeyGenerator::keyPatternByUserId).toList();
    }

}
