package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum;
import cc.wdev.platform.system.core.cache.UserAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.converter.AuthorityConverter;
import cc.wdev.platform.system.core.domain.entity.AuthorityEntity;
import cc.wdev.platform.system.core.domain.vo.AuthorityVo;
import cc.wdev.platform.system.core.repository.AuthorityRepository;
import cc.wdev.platform.system.core.service.AuthorityService;
import cc.wdev.platform.system.core.service.EntityAuthorityService;
import cc.wdev.platform.system.core.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author elvea
 * @see AuthorityService
 * @see BaseCachingEntityService
 */
@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl extends BaseCachingEntityService<AuthorityEntity, Long, AuthorityRepository>
    implements AuthorityService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleCacheKeyGenerator.builder().prefix(SystemCacheConstants.AUTHORITY).build();

    private final UserRoleService userRoleService;

    private final EntityAuthorityService entityAuthorityService;

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see AuthorityService#findByUserId(Long)
     */
    @Override
    public List<AuthorityEntity> findByUserId(Long userId) {
        if (!ObjectUtils.isValidId(userId)) {
            return Collections.emptyList();
        }

        Collection<Long> authorityIds = getCacheService().get(UserAuthorityCacheKeyGenerator.keyByUserId(userId), _ -> {
            Set<Long> roleIds = this.userRoleService.findRoleIdsByUserId(userId);
            if (CollectionUtils.isEmpty(roleIds)) {
                return Collections.emptyList();
            }
            // 租户权限
            Set<Long> tenantAuthorityIds = this.entityAuthorityService.findAuthorityIds(EntityAuthorityBizTypeEnum.TENANT, TenantContext.getTenantId());
            if (CollectionUtils.isEmpty(tenantAuthorityIds)) {
                tenantAuthorityIds = Collections.emptySet();
            }
            // 角色权限
            Set<Long> roleAuthorityIds = this.entityAuthorityService.findAuthorityIds(EntityAuthorityBizTypeEnum.ROLE, roleIds);
            if (CollectionUtils.isEmpty(roleAuthorityIds)) {
                roleAuthorityIds = Collections.emptySet();
            }
            return roleAuthorityIds.stream().filter(tenantAuthorityIds::contains).collect(Collectors.toSet());
        });

        return this.findCacheByIds(authorityIds).stream()
            .filter(e -> e.getActive() == BooleanTypeEnum.TRUE.getValue().intValue())
            .collect(Collectors.toList());
    }

    @Override
    public List<AuthorityVo> findAuthorityVoList() {
        // 处理权限范围类型
        List<String> scopes = BizScopeTypeEnum.getBizScopeTypes();

        // 权限数据查询出来后，遍历转换并排序
        return this.lambdaQueryWrapper()
            .in(AuthorityEntity::getAuthorityScopeType, scopes)
            .eq(AuthorityEntity::getActive, ActiveTypeEnum.getEnabledValue())
            .list()
            .stream()
            .map(AuthorityConverter.INSTANCE::entityToVo)
            .sorted(Comparator.comparing(AuthorityVo::getIdx, Comparator.nullsFirst(Comparator.naturalOrder())))
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Long> findAuthorityIds(String bizType, Long bizId) {
        Set<Long> authorityIds = entityAuthorityService.findAuthorityIds(bizType, bizId);
        List<AuthorityEntity> entities = this.findCacheByIds(authorityIds);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream()
            .filter(a -> ActiveTypeEnum.ENABLED.getValue().equals(a.getActive()))
            .map(AuthorityEntity::getId).toList();
    }

}
