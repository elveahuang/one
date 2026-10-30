package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.RoleTypeEnum;
import cc.wdev.platform.system.core.cache.UserAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.vo.UserRoleTypeVo;
import cc.wdev.platform.system.core.service.RoleService;
import cc.wdev.platform.system.core.service.UserRoleTypeService;
import cc.wdev.platform.system.core.service.UserRoleService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户身份切换实现。
 *
 * <p>当前身份存在缓存中（按「租户 + 用户」维度），切换后立即影响权限计算：
 * {@code AuthorityServiceImpl#findByUserId} 会读取 {@code RoleTypeContext} 并按类型过滤角色。</p>
 *
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleTypeServiceImpl implements UserRoleTypeService {

    /**
     * 当前身份缓存键前缀；与权限缓存放在同一 db，登出/切换时一并清理
     */
    private static final String CURRENT_ROLE_TYPE_PREFIX = "user-role-type";

    /**
     * 当前身份的存活时间；访问令牌默认 60 分钟，留一点余量
     */
    private static final Duration CURRENT_ROLE_TYPE_TTL = Duration.ofHours(12);

    /**
     * 参与身份切换的类型优先级：越靠前权限越大，用于「未切换时默认选最高」
     */
    private static final List<String> PRIORITY = List.of(
        RoleTypeEnum.PLATFORM_ADMINISTRATOR.getValue(),
        RoleTypeEnum.SYSTEM_ADMINISTRATOR.getValue(),
        RoleTypeEnum.USER.getValue()
    );

    private final RoleService roleService;

    private final UserRoleService userRoleService;

    private final CacheService cacheService;

    @Override
    public UserRoleTypeVo getRoleType(Long userId) {
        List<String> owned = this.findOwnedRoleTypes(userId);
        List<String> available = this.buildAvailable(owned);
        String current = this.getCurrentRoleType(userId);
        return UserRoleTypeVo.builder()
            .current(current != null ? current : this.defaultRoleType(available))
            .owned(owned)
            .available(available)
            .switchable(available.size() > 1)
            .build();
    }

    @Override
    public UserRoleTypeVo switchRoleType(Long userId, String roleType) {
        List<String> owned = this.findOwnedRoleTypes(userId);
        List<String> available = this.buildAvailable(owned);

        if (StringUtils.isEmpty(roleType)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_NOT_PRESENT);
        }
        String target = roleType.trim().toUpperCase();

        // 只能切换到本人真实拥有、且允许切换的类型，防止伪造
        if (!available.contains(target)) {
            log.warn("[RoleType] user [{}] attempted to switch to unauthorized role type [{}], owned={}", userId, target, owned);
            throw new ServiceException(ResponseCodeEnum.OPERATION_PERMISSION_ERROR);
        }

        cacheService.set(this.cacheKey(userId), target, CURRENT_ROLE_TYPE_TTL);
        // 切换后权限集合变化，清掉该用户全部身份维度的权限缓存，避免读到旧值
        this.evictAuthorityCache(userId);

        log.info("[RoleType] user [{}] switched role type to [{}]", userId, target);
        return UserRoleTypeVo.builder()
            .current(target)
            .owned(owned)
            .available(available)
            .switchable(available.size() > 1)
            .build();
    }

    @Override
    public String getCurrentRoleType(Long userId) {
        if (!ObjectUtils.isValidId(userId)) {
            return null;
        }
        String value = cacheService.get(this.cacheKey(userId));
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        // 记录的类型可能因角色变更失效，需回查是否仍拥有
        return this.findOwnedRoleTypes(userId).contains(value) ? value : null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>安全要点：<b>服务端记录的当前身份优先于请求头</b>。用户一旦通过接口切换过身份，
     * 该选择就固化在服务端，请求头只能在不存在的记录时才起作用（用于多标签页等场景）。
     * 否则用户切到低权限身份后，只要伪造 {@code x-role-type} 就能恢复高权限，收窄形同虚设。</p>
     */
    @Override
    public String resolveEffectiveRoleType(Long userId, String requestedRoleType) {
        if (!ObjectUtils.isValidId(userId)) {
            return null;
        }
        List<String> owned = this.findOwnedRoleTypes(userId);
        if (CollectionUtils.isEmpty(owned)) {
            return null;
        }

        // 1) 服务端已记录的切换结果最优先，且必须是本人仍拥有的类型
        String stored = cacheService.get(this.cacheKey(userId));
        if (StringUtils.isNotEmpty(stored)) {
            if (owned.contains(stored)) {
                return stored;
            }
            // 记录已失效（角色被移除等），丢弃并继续走后面的分支
            log.info("[RoleType] user [{}] stored role type [{}] no longer owned, dropped", userId, stored);
        }

        // 2) 没有服务端记录时才接受请求头，且必须属于本人拥有的类型
        if (StringUtils.isNotEmpty(requestedRoleType)) {
            String normalized = requestedRoleType.trim().toUpperCase();
            if (owned.contains(normalized)) {
                return normalized;
            }
            log.warn("[RoleType] user [{}] requested role type [{}] not owned, ignored", userId, requestedRoleType);
        }

        // 3) 从未切换过：多身份用户自动落到默认（最高）身份。
        //    否则「展示的是平台管理员、实际生效的却是全部角色合并」，
        //    用户会看到权限在切换前后莫名少了几项。
        List<String> available = this.buildAvailable(owned);
        if (available.size() > 1) {
            String fallback = available.getFirst();
            cacheService.set(this.cacheKey(userId), fallback, CURRENT_ROLE_TYPE_TTL);
            log.info("[RoleType] user [{}] initialized role type to default [{}]", userId, fallback);
            return fallback;
        }

        // 单身份或纯会员：不做类型过滤，沿用原有「全部角色合并」行为
        return null;
    }

    @Override
    public void clearRoleType(Long userId) {
        if (!ObjectUtils.isValidId(userId)) {
            return;
        }
        cacheService.delete(this.cacheKey(userId));
    }

    /**
     * 用户在**当前租户**下拥有的角色类型（去重、按优先级排序）。
     */
    private List<String> findOwnedRoleTypes(Long userId) {
        Set<Long> roleIds = userRoleService.findRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }
        List<RoleEntity> roles = roleService.findCacheByIds(roleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return Lists.newArrayList();
        }
        return roles.stream()
            .map(RoleEntity::getBizType)
            .filter(StringUtils::isNotEmpty)
            .distinct()
            .sorted(Comparator.comparingInt(this::priority))
            .collect(Collectors.toList());
    }

    /**
     * 可切换类型：排除 MEMBER，且只保留优先级列表中已知的类型。
     */
    private List<String> buildAvailable(List<String> owned) {
        return owned.stream()
            .filter(t -> !RoleTypeEnum.MEMBER.getValue().equalsIgnoreCase(t))
            .filter(PRIORITY::contains)
            .collect(Collectors.toList());
    }

    private int priority(String roleType) {
        int idx = PRIORITY.indexOf(roleType);
        return idx < 0 ? Integer.MAX_VALUE : idx;
    }

    private String defaultRoleType(List<String> available) {
        return CollectionUtils.isEmpty(available) ? null : available.getFirst();
    }

    private String cacheKey(Long userId) {
        return CURRENT_ROLE_TYPE_PREFIX + ":" + TenantContext.getTenantId() + ":uid_" + userId;
    }

    /**
     * 清理该用户在任意身份维度下的权限缓存。
     */
    private void evictAuthorityCache(Long userId) {
        try {
            String pattern = UserAuthorityCacheKeyGenerator.keyByUserId(userId).getKey() + "*";
            Set<String> keys = cacheService.scan(pattern);
            if (CollectionUtils.isNotEmpty(keys)) {
                keys.forEach(cacheService::delete);
            }
        } catch (Exception e) {
            // 清理失败不阻断切换：新身份写入后，旧缓存键已带不同后缀，不会命中
            log.warn("[RoleType] evict authority cache failed for user [{}]", userId, e);
        }
    }

}
