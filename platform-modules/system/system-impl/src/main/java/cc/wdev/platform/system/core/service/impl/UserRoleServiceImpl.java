package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.core.cache.UserAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.cache.UserRoleCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.entity.UserRoleEntity;
import cc.wdev.platform.system.core.repository.UserRoleRepository;
import cc.wdev.platform.system.core.service.UserRoleService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.utils.ObjectUtils.isValidId;

/**
 * @author elvea
 * @see UserRoleService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
public class UserRoleServiceImpl
    extends BaseCachingEntityService<UserRoleEntity, Long, UserRoleRepository>
    implements UserRoleService {

    private final CacheKeyGenerator cacheKeyGenerator = new UserRoleCacheKeyGenerator();

    /**
     * @see CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see UserRoleService#findByUserId(Long)
     */
    @Override
    public List<UserRoleEntity> findByUserId(Long userId) {
        return lambdaQueryWrapper().eq(UserRoleEntity::getUserId, userId).list();
    }

    @Override
    public Map<Long, List<UserRoleEntity>> batchRole(Collection<Long> userIds) {
        List<UserRoleEntity> entities = this.lambdaQueryWrapper().in(UserRoleEntity::getUserId, userIds).list();
        return entities.stream().collect(Collectors.groupingBy(UserRoleEntity::getUserId));
    }

    /**
     * @see UserRoleService#findRoleIdsByUserId(Long)
     */
    @Override
    public Set<Long> findRoleIdsByUserId(Long userId) {
        if (!isValidId(userId)) {
            return Collections.emptySet();
        }
        return getCacheService().get(UserRoleCacheKeyGenerator.keyByUserId(userId), _ ->
            this.findByUserId(userId).stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toSet())
        );
    }

    /**
     * @see UserRoleService#findRoleIdsByUserId(Long)
     */
    @Override
    public Set<Long> findUserIdsByRoleId(Long roleId) {
        if (!isValidId(roleId)) {
            return Collections.emptySet();
        }
        return this.findUserIdsByRoleIds(List.of(roleId));
    }

    /**
     * @see UserRoleService#findUserIdsByRoleIds(List)
     */
    @Override
    public Set<Long> findUserIdsByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptySet();
        }
        return lambdaQueryWrapper()
            .select(UserRoleEntity::getUserId)
            .in(UserRoleEntity::getRoleId, roleIds)
            .list()
            .stream()
            .map(UserRoleEntity::getUserId)
            .collect(Collectors.toSet());
    }

    /**
     * @see UserRoleService#deleteByRoleId(Long)
     */
    @Override
    public void deleteByRoleId(Long roleId) {
        if (isValidId(roleId)) {
            this.lambdaUpdateWrapper()
                .eq(UserRoleEntity::getRoleId, roleId)
                .remove();
        }
    }

    /**
     * @see UserRoleService#deleteByUserIds(List)
     */
    @Override
    public void deleteByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isNotEmpty(roleIds)) {
            this.lambdaUpdateWrapper()
                .in(UserRoleEntity::getRoleId, roleIds)
                .remove();
        }
    }

    /**
     * @see UserRoleService#deleteByUserIds(List)
     */
    @Override
    public void deleteByUserIds(List<Long> userIds) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            this.lambdaUpdateWrapper()
                .in(UserRoleEntity::getUserId, userIds)
                .remove();
        }
        List<UserRoleEntity> userRoleEntityList = this.lambdaQueryWrapper()
            .select(UserRoleEntity::getId, UserRoleEntity::getUserId)
            .in(UserRoleEntity::getUserId, userIds)
            .list();
        if (CollectionUtils.isEmpty(userRoleEntityList)) {
            log.error("deleteUserRole Invalid userRoleEntityList.");
            return;
        }
        this.deleteBatch(userRoleEntityList);
    }

    /**
     * @see UserRoleService#saveUserRole(Long, List)
     */
    @Override
    public void saveUserRole(Long userId, List<Long> roleIds) {
        if (!ObjectUtils.isValidId(userId)) {
            log.error("bindUserRole Invalid userIds.");
            return;
        }
        // 删除关联
        this.deleteByUserIds(List.of(userId));

        // 保存关联
        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<UserRoleEntity> userRoles = Lists.newArrayListWithCapacity(roleIds.size());
            for (Long roleId : roleIds) {
                userRoles.add(UserRoleEntity.builder().userId(userId).roleId(roleId).build());
            }
            this.saveBatch(userRoles);
        }

        // 删除缓存
        this.deleteCacheByUserIds(List.of(userId));
    }

    /**
     * @see UserRoleService#deleteCacheByUserIds(Collection)
     */
    @Override
    public void deleteCacheByUserIds(Collection<Long> userIds) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            getCacheService().delete(UserRoleCacheKeyGenerator.keysByUserIds(userIds));
            getCacheService().delete(UserAuthorityCacheKeyGenerator.keysByUserIds(userIds));
        }
    }

    @Override
    public void deleteCache(UserRoleEntity model) {
        if (hasId(model) && isValidId(model.getId())) {
            this.deleteCacheByUserIds(List.of(model.getUserId()));
        }
    }

    @Override
    public void setCache(UserRoleEntity model) {
        //
    }

}
