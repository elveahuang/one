package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum;
import cc.wdev.platform.system.core.cache.EntityAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.entity.EntityAuthorityEntity;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.repository.EntityAuthorityRepository;
import cc.wdev.platform.system.core.service.EntityAuthorityService;
import cc.wdev.platform.system.core.service.RoleService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityAuthorityServiceImpl
    extends BaseCachingEntityService<EntityAuthorityEntity, Long, EntityAuthorityRepository>
    implements EntityAuthorityService {

    private final EntityAuthorityCacheKeyGenerator cacheKeyGenerator = new EntityAuthorityCacheKeyGenerator();

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    private RoleService roleService;

    /**
     * @see EntityAuthorityService#findAuthorityIds(String, Long)
     */
    @Override
    public Set<Long> findAuthorityIds(String bizType, Long bizId) {
        EntityAuthorityBizTypeEnum bizTypeEnum = BaseEnum.getEnumByValue(bizType, EntityAuthorityBizTypeEnum.class, EntityAuthorityBizTypeEnum.NONE);
        if (StringUtils.isBlank(bizType) || ObjectUtils.isInvalidId(bizId) || EntityAuthorityBizTypeEnum.NONE.equals(bizTypeEnum)) {
            return Collections.emptySet();
        }

        Set<Long> authorityIds = this.getCacheService().get(cacheKeyGenerator.byBizType(bizType, bizId), _ ->
            this.lambdaQueryWrapper()
                .select(EntityAuthorityEntity::getAuthorityId)
                .eq(EntityAuthorityEntity::getBizType, bizType)
                .eq(EntityAuthorityEntity::getEntityId, bizId)
                .eq(EntityAuthorityEntity::getActive, ActiveTypeEnum.getEnabledValue())
                .list()
                .stream()
                .map(EntityAuthorityEntity::getAuthorityId)
                .collect(Collectors.toSet())
        );
        return nvl(authorityIds, Collections.emptySet());
    }

    /**
     * @see EntityAuthorityService#findAuthorityIds(EntityAuthorityBizTypeEnum, Long)
     */
    @Override
    public Set<Long> findAuthorityIds(EntityAuthorityBizTypeEnum bizTypeEnum, Long bizId) {
        if (ObjectUtils.isInvalidId(bizId) || EntityAuthorityBizTypeEnum.NONE.equals(bizTypeEnum)) {
            return Collections.emptySet();
        }

        return this.findAuthorityIds(bizTypeEnum.getValue(), bizId);
    }

    /**
     * @see EntityAuthorityService#findAuthorityIds(EntityAuthorityBizTypeEnum, Long)
     */
    @Override
    public Set<Long> findAuthorityIds(EntityAuthorityBizTypeEnum bizTypeEnum, Collection<Long> bizIds) {
        if (CollectionUtils.isEmpty(bizIds) || ObjectUtils.isEmpty(bizTypeEnum) || EntityAuthorityBizTypeEnum.NONE.equals(bizTypeEnum)) {
            return Collections.emptySet();
        }

        // 遍历数组优先从缓存中获取权限
        Set<Long> authorityIds = Sets.newHashSet();
        bizIds.forEach(bizId -> authorityIds.addAll(this.findAuthorityIds(bizTypeEnum, bizId)));
        return authorityIds;
    }

    /**
     * @see EntityAuthorityService#deleteAuthority(EntityAuthorityBizTypeEnum, Long)
     */
    @Override
    public void deleteAuthority(@NonNull EntityAuthorityBizTypeEnum bizTypeEnum, Long bizId) {
        if (ObjectUtils.isValidId(bizId) && !EntityAuthorityBizTypeEnum.NONE.equals(bizTypeEnum)) {
            this.lambdaUpdateWrapper()
                .eq(EntityAuthorityEntity::getBizType, bizTypeEnum.getValue())
                .eq(EntityAuthorityEntity::getEntityId, bizId)
                .remove();
        }
    }

    /**
     * @see EntityAuthorityService#deleteAuthority(EntityAuthorityBizTypeEnum, List)
     */
    @Override
    public void deleteAuthority(@NonNull EntityAuthorityBizTypeEnum bizTypeEnum, List<Long> bizIds) {
        if (CollectionUtils.isNotEmpty(bizIds) && !EntityAuthorityBizTypeEnum.NONE.equals(bizTypeEnum)) {
            this.lambdaUpdateWrapper()
                .eq(EntityAuthorityEntity::getBizType, bizTypeEnum.getValue())
                .in(EntityAuthorityEntity::getEntityId, bizIds)
                .remove();
        }
    }

    /**
     * @see EntityAuthorityService#saveAuthority(EntityAuthorityBizTypeEnum, Long, Collection)
     */
    @Override
    public boolean saveAuthority(EntityAuthorityBizTypeEnum bizTypeEnum, Long bizId, Collection<Long> authorityIds) {
        // 检查关联是否有变更
        Set<Long> beforeAuthorityIds = this.findAuthorityIds(bizTypeEnum, bizId);
        Set<Long> afterAuthorityIds = Sets.newHashSet(nvl(authorityIds, Collections.emptySet()));
        if (afterAuthorityIds.equals(beforeAuthorityIds)) {
            log.info("Authority for [{}] [{}] not change.", bizTypeEnum.getLabelKey(), bizId);
            return false;
        }

        // 删除旧关联
        this.deleteAuthority(bizTypeEnum, bizId);

        // 删除权限缓存
        getCacheService().delete(cacheKeyGenerator.byBizType(bizTypeEnum.getValue(), bizId));
        // 刪除角色缓存
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(bizId);
        roleService.deleteCache(roleEntity);

        // 保存关联
        String bizType = bizTypeEnum.getValue();
        List<EntityAuthorityEntity> entities = Lists.newArrayListWithCapacity(afterAuthorityIds.size());
        for (Long authorityId : afterAuthorityIds) {
            EntityAuthorityEntity entity = EntityAuthorityEntity.builder()
                .entityId(bizId)
                .bizType(bizType)
                .authorityId(authorityId)
                .build();
            entity.setTenantId(TenantContext.getTenantId());
            entities.add(entity);
        }

        if (CollectionUtils.isNotEmpty(entities)) {
            this.insertBatch(entities);
        }
        return true;
    }

    @Override
    public void setCache(EntityAuthorityEntity model) {
        //
    }

    @Override
    public void deleteCache(EntityAuthorityEntity model) {
        //
    }

    @Autowired
    @Lazy
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
