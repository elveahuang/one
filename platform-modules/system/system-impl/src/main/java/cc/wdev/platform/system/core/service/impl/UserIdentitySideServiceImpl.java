package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.IdentitySideEnum;
import cc.wdev.platform.system.core.cache.UserAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.vo.UserIdentitySideVo;
import cc.wdev.platform.system.core.service.RoleService;
import cc.wdev.platform.system.core.service.UserIdentitySideService;
import cc.wdev.platform.system.core.service.UserRoleService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户身份端切换实现。
 *
 * <p>身份被收敛为两个端：管理端（平台管理员/系统管理员/普通用户，权限取并集）与会员端（仅会员）。
 * 当前端存在缓存中（按「租户 + 用户」维度），切换后立即影响权限计算：
 * {@code AuthorityServiceImpl#findByUserId} 会读取 {@code IdentitySideContext} 并按端内角色类型过滤。</p>
 *
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserIdentitySideServiceImpl implements UserIdentitySideService {

    /**
     * 当前身份端缓存键前缀；与权限缓存放在同一 db，登出/切换时一并清理
     */
    private static final String CURRENT_IDENTITY_SIDE_PREFIX = "user-identity-side";

    /**
     * 当前身份端的存活时间。
     *
     * <p>必须覆盖「刷新令牌」的整个有效期（默认 14 天），否则用户切换身份后，
     * 记录先于会话过期，{@code resolveEffectiveIdentitySide} 会回落到默认端，
     * 相当于把用户主动收窄的身份静默提升回去。登出会显式清理该键，
     * 因此不需要靠 TTL 做会话回收。</p>
     */
    private static final Duration CURRENT_IDENTITY_SIDE_TTL = Duration.ofDays(15);

    private final RoleService roleService;

    private final UserRoleService userRoleService;

    private final CacheService cacheService;

    @Override
    public UserIdentitySideVo getIdentitySide(Long userId) {
        List<String> owned = this.findOwnedSides(userId);
        String current = this.resolveEffectiveIdentitySide(userId, null);
        return UserIdentitySideVo.builder()
            .current(current != null ? current : this.defaultSide(owned))
            .owned(owned)
            .available(owned)
            .switchable(owned.size() > 1)
            .build();
    }

    @Override
    public UserIdentitySideVo switchIdentitySide(Long userId, String side) {
        List<String> owned = this.findOwnedSides(userId);

        if (StringUtils.isEmpty(side)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_NOT_PRESENT);
        }
        IdentitySideEnum target = IdentitySideEnum.of(side);
        if (target == null) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }

        // 只能切换到本人真实拥有的端，防止伪造
        if (!owned.contains(target.getValue())) {
            log.warn("[IdentitySide] user [{}] attempted to switch to unowned side [{}], owned={}", userId, target.getValue(), owned);
            throw new ServiceException(ResponseCodeEnum.OPERATION_PERMISSION_ERROR);
        }

        cacheService.set(this.cacheKey(userId), target.getValue(), CURRENT_IDENTITY_SIDE_TTL);
        // 切换后权限集合变化，清掉该用户全部身份维度的权限缓存，避免读到旧值
        this.evictAuthorityCache(userId);

        log.info("[IdentitySide] user [{}] switched side to [{}]", userId, target.getValue());
        return UserIdentitySideVo.builder()
            .current(target.getValue())
            .owned(owned)
            .available(owned)
            .switchable(owned.size() > 1)
            .build();
    }

    /**
     * {@inheritDoc}
     *
     * <p>安全要点：<b>服务端记录的当前端优先于请求头</b>。用户一旦通过接口切换过身份，
     * 该选择就固化在服务端，请求头只在不存在的记录时才起作用。
     * 否则用户切到会员端后，只要伪造 {@code x-identity-side} 就能恢复管理端权限，收窄形同虚设。</p>
     */
    @Override
    public String resolveEffectiveIdentitySide(Long userId, String requestedIdentitySide) {
        if (!ObjectUtils.isValidId(userId)) {
            return null;
        }
        List<String> owned = this.findOwnedSides(userId);
        if (CollectionUtils.isEmpty(owned)) {
            return null;
        }

        // 1) 服务端已记录的切换结果最优先，且必须是本人仍拥有的端
        String stored = cacheService.get(this.cacheKey(userId));
        if (StringUtils.isNotEmpty(stored)) {
            if (owned.contains(stored)) {
                return stored;
            }
            log.info("[IdentitySide] user [{}] stored side [{}] no longer owned, dropped", userId, stored);
        }

        // 2) 没有服务端记录时才接受请求头，且必须属于本人拥有的端
        if (StringUtils.isNotEmpty(requestedIdentitySide)) {
            IdentitySideEnum requested = IdentitySideEnum.of(requestedIdentitySide);
            if (requested != null && owned.contains(requested.getValue())) {
                return requested.getValue();
            }
            log.warn("[IdentitySide] user [{}] requested side [{}] not owned, ignored", userId, requestedIdentitySide);
        }

        // 3) 从未切换过：拥有两端时自动落到管理端（包含全部管理类角色，权限最全）。
        //    否则「展示的是管理端、实际生效的却是全部角色合并」，切换前后权限会莫名变化。
        if (owned.size() > 1) {
            String fallback = this.defaultSide(owned);
            cacheService.set(this.cacheKey(userId), fallback, CURRENT_IDENTITY_SIDE_TTL);
            log.info("[IdentitySide] user [{}] initialized side to default [{}]", userId, fallback);
            return fallback;
        }

        // 只属于单一端：不做端过滤，沿用原有「全部角色合并」行为
        return null;
    }

    @Override
    public void clearIdentitySide(Long userId) {
        if (!ObjectUtils.isValidId(userId)) {
            return;
        }
        cacheService.delete(this.cacheKey(userId));
    }

    /**
     * 用户在**当前租户**下拥有的端，按固定顺序（管理端在前）返回。
     */
    private List<String> findOwnedSides(Long userId) {
        Set<Long> roleIds = userRoleService.findRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }
        List<RoleEntity> roles = roleService.findCacheByIds(roleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return Lists.newArrayList();
        }
        Set<IdentitySideEnum> ownedSides = roles.stream()
            .map(RoleEntity::getBizType)
            .map(IdentitySideEnum::ofRoleType)
            .filter(side -> side != null)
            .collect(Collectors.toSet());
        // 保持枚举声明顺序：ADMIN 在前
        return Arrays.stream(IdentitySideEnum.values())
            .filter(ownedSides::contains)
            .map(IdentitySideEnum::getValue)
            .collect(Collectors.toList());
    }

    private String defaultSide(List<String> owned) {
        return CollectionUtils.isEmpty(owned) ? null : owned.getFirst();
    }

    private String cacheKey(Long userId) {
        return CURRENT_IDENTITY_SIDE_PREFIX + ":" + TenantContext.getTenantId() + ":uid_" + userId;
    }

    /**
     * 清理该用户在任意身份维度下的权限缓存。
     */
    private void evictAuthorityCache(Long userId) {
        try {
            String pattern = UserAuthorityCacheKeyGenerator.keyPatternByUserId(userId);
            Set<String> keys = cacheService.scan(pattern);
            if (CollectionUtils.isNotEmpty(keys)) {
                keys.forEach(cacheService::delete);
            }
        } catch (Exception e) {
            // 清理失败不阻断切换：新身份写入后，旧缓存键已带不同后缀，不会命中
            log.warn("[IdentitySide] evict authority cache failed for user [{}]", userId, e);
        }
    }

}
