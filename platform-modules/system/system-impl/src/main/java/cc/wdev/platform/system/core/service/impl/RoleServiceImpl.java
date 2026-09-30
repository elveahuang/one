package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum;
import cc.wdev.platform.system.core.cache.EntityAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.cache.UserAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.cache.UserRoleCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.converter.RoleConverter;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.entity.UserRoleEntity;
import cc.wdev.platform.system.core.domain.form.RoleForm;
import cc.wdev.platform.system.core.domain.request.RoleSearchRequest;
import cc.wdev.platform.system.core.domain.vo.RoleVo;
import cc.wdev.platform.system.core.repository.RoleRepository;
import cc.wdev.platform.system.core.service.EntityAuthorityService;
import cc.wdev.platform.system.core.service.RoleService;
import cc.wdev.platform.system.core.service.UserRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum.ROLE;

/**
 * @author elvea
 * @see RoleService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseCachingEntityService<RoleEntity, Long, RoleRepository> implements RoleService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(SystemCacheConstants.ROLE);

    private final UserRoleService userRoleService;

    private final EntityAuthorityService entityAuthorityService;

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see RoleService#findRoleById(Long)
     */
    @Override
    public RoleVo findRoleById(Long roleId) {
        return RoleConverter.INSTANCE.entityToVo(this.findCacheById(roleId));
    }

    /**
     * @see RoleService#findByUserId(Long)
     */
    @Override
    public List<RoleEntity> findByUserId(Long userId) {
        if (ObjectUtils.isInvalidId(userId)) {
            return Collections.emptyList();
        }
        Collection<Long> roleIds = userRoleService.findRoleIdsByUserId(userId);
        return this.findCacheByIds(roleIds);
    }

    @Override
    public Map<Long, List<RoleEntity>> batchRole(Collection<Long> userIds) {
        Map<Long, List<UserRoleEntity>> userRoleMap = userRoleService.batchRole(userIds);
        if (CollectionUtils.isEmpty(userRoleMap)) {
            return Collections.emptyMap();
        }

        Set<Long> roleIds = Sets.newHashSet();
        userRoleMap.forEach((_, userRoles) -> {
            if (CollectionUtils.isEmpty(userRoles)) {
                return;
            }
            for (UserRoleEntity userRole : userRoles) {
                roleIds.add(userRole.getRoleId());
            }
        });
        List<RoleEntity> entities = this.findCacheByIds(roleIds);
        Map<Long, RoleEntity> entityMap = entities.stream().collect(Collectors.toMap(RoleEntity::getId, (e) -> e, (e, _) -> e));
        if (CollectionUtils.isEmpty(userRoleMap)) {
            return Collections.emptyMap();
        }

        Map<Long, List<RoleEntity>> map = Maps.newHashMapWithExpectedSize(userRoleMap.size());
        userRoleMap.forEach((userId, userRoles) -> {
            if (CollectionUtils.isEmpty(userRoles)) {
                return;
            }
            List<RoleEntity> roles = Lists.newArrayListWithCapacity(userRoles.size());
            for (UserRoleEntity userRole : userRoles) {
                RoleEntity entity = entityMap.get(userRole.getRoleId());
                if (entity == null) {
                    continue;
                }
                roles.add(entity);
            }
            map.put(userId, roles);
        });

        return map;
    }

    /**
     * @see RoleService#saveRole(RoleForm)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(@NonNull RoleForm form) {
        RoleEntity entity = this.checkExistsOrReturn(form.getId(), new RoleEntity(), ResponseCodeEnum.ROLE__NOT_PRESENT);
        if (ObjectUtils.isInvalidId(form.getId())) {
            form.setId(null);
        }
        RoleConverter.INSTANCE.formToEntity(form, entity);
        // 如果编号为空，自动生成唯一编号
        if (StringUtils.isEmpty(form.getCode())) {
            entity.setCode(generateCode("ROLE"));
        }
        // 把当前租户其他角色设置为非默认角色
        if (BooleanTypeEnum.isTrueValue(form.getDefaultInd())) {
            List<RoleEntity> defaultRoles = lambdaQueryWrapper().eq(RoleEntity::getDefaultInd, BooleanTypeEnum.getTrueValue()).list();
            if (CollectionUtils.isNotEmpty(defaultRoles)) {
                defaultRoles.forEach(role -> role.setDefaultInd(BooleanTypeEnum.getFalseValue()));
                saveBatch(defaultRoles);
            }
        }

        this.save(entity);
    }

    /**
     * @see RoleService#saveRole(RoleForm)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleAuthority(RoleForm form) {
        RoleEntity entity = this.checkExistsOrFail(form.getId(), ResponseCodeEnum.ROLE__NOT_PRESENT);
        this.entityAuthorityService.saveAuthority(ROLE, entity.getId(), form.getAuthorityIds());
    }

    /**
     * @see RoleService#saveRole(RoleForm)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        RoleEntity entity = this.checkExistsOrFail(roleId, ResponseCodeEnum.ROLE__NOT_PRESENT);

        // 删除用户角色
        this.userRoleService.deleteByRoleId(roleId);
        // 删除角色权限
        this.entityAuthorityService.deleteAuthority(ROLE, roleId);

        this.delete(entity);
    }

    /**
     * @see RoleService#saveRole(RoleForm)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRoles(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        // 现在查询关联该角色的用户
        Collection<Long> userIds = userRoleService.findUserIdsByRoleIds(roleIds);

        if (CollectionUtils.isNotEmpty(userIds)) {
            // 删除该角色和用户的关联
            this.userRoleService.deleteByRoleIds(roleIds);

            // 清理缓存
            getCacheService().delete(UserAuthorityCacheKeyGenerator.keysByUserIds(userIds));
            getCacheService().delete(UserRoleCacheKeyGenerator.keysByUserIds(userIds));
        }

        // 删除角色和权限的关联和缓存
        this.entityAuthorityService.deleteAuthority(ROLE, roleIds);
        getCacheService().delete(EntityAuthorityCacheKeyGenerator.keysByBizIds(EntityAuthorityBizTypeEnum.ROLE.getValue(), roleIds));

        // 批量删除角色
        this.deleteBatchById(roleIds);
    }

    /**
     * @see RoleService#findRolePage(RoleSearchRequest)
     */
    @Override
    public Page<RoleVo> findRolePage(RoleSearchRequest req) {
        IPage<RoleEntity> page = this.lambdaQueryWrapper().and(StringUtils.isNotEmpty(req.getQ()), wrapper ->
                wrapper.like(RoleEntity::getCode, req.getQ()).or().like(RoleEntity::getTitle, req.getQ()))
            .eq(RoleEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(req.getPageable()));

        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<RoleVo> list = page.getRecords().stream().map(RoleConverter.INSTANCE::entityToVo).toList();
            return MyBatisPlusUtils.toSpringDataPage(page, list);
        }
        return SpringDataUtils.emptyPage();
    }

    /**
     * @see RoleService#findAllRoles()
     */
    @Override
    public List<RoleVo> findAllRoles() {
        return this.lambdaQueryWrapper()
            .eq(RoleEntity::getStatus, StatusTypeEnum.ON.getValue())
            .eq(RoleEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list()
            .stream()
            .map(RoleConverter.INSTANCE::entityToVo)
            .toList();
    }

    /**
     * @see RoleService#getRoleVo(Long)
     */
    @Override
    public RoleVo getRoleVo(Long roleId) {
        RoleEntity entity = this.checkExistsOrFail(roleId, ResponseCodeEnum.ROLE__NOT_PRESENT);
        return RoleConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see RoleService#getDefaultRole()
     */
    @Override
    public RoleVo getDefaultRole() {
        RoleEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(RoleEntity::getStatus, StatusTypeEnum.ON.getValue())
            .eq(RoleEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
        return RoleConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * 删除角色相关缓存
     */
    private void deleteCacheById(Long roleId) {
        // 删除角色权限缓存
        getCacheService().delete(EntityAuthorityCacheKeyGenerator.keyByEntity(ROLE.getValue(), roleId));

        // 查询角色关联用户缓存
        Collection<Long> userIds = userRoleService.findUserIdsByRoleId(roleId);
        if (CollectionUtils.isNotEmpty(userIds)) {
            getCacheService().delete(UserAuthorityCacheKeyGenerator.keysByUserIds(userIds));
        }
    }

    @Override
    public void deleteCache(RoleEntity model) {
        super.deleteCache(model);

        // 删除关联缓存
        if (ObjectUtils.isValidId(model)) {
            this.deleteCacheById(model.getId());
        }
    }

}
