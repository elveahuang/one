package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.entity.UserRoleEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author elvea
 */
public interface UserRoleService extends CachingEntityService<UserRoleEntity, Long> {

    /**
     * @param userId 用户ID
     * @return 用户角色关联
     */
    List<UserRoleEntity> findByUserId(Long userId);

    Map<Long, List<UserRoleEntity>> batchRole(Collection<Long> userIds);

    /**
     * @param userId 用户ID
     * @return 角色ID
     */
    Set<Long> findRoleIdsByUserId(Long userId);

    /**
     * 按角色类型集合筛选用户的角色ID。
     *
     * <p>身份切换以「端」为单位：管理端一次要取三类角色（平台管理员/系统管理员/普通用户）的并集，
     * 因此这里接受类型集合而不是单一类型。</p>
     *
     * @param userId    用户ID
     * @param roleTypes 角色类型集合（sys_role.biz_type）；为空则等价于 {@link #findRoleIdsByUserId(Long)}
     * @return 属于这些类型的角色ID
     */
    Set<Long> findRoleIdsByUserId(Long userId, Collection<String> roleTypes);

    /**
     * 根据角色ID获取用户ID数组
     *
     * @param roleId 角色id
     * @return 用户ids
     */
    Set<Long> findUserIdsByRoleId(Long roleId);

    /**
     * 根据角色ID数组获取用户ID数组
     *
     * @param roleIds 角色id
     * @return 用户ids
     */
    Set<Long> findUserIdsByRoleIds(List<Long> roleIds);

    /**
     * 根据角色ID删除用户角色
     *
     * @param roleId 角色ID
     */
    void deleteByRoleId(Long roleId);

    /**
     * 根据角色ID数组删除用户角色
     *
     * @param roleIds 角色ID数组
     */
    void deleteByRoleIds(List<Long> roleIds);

    /**
     * 用户关联角色
     */
    void saveUserRole(Long userId, List<Long> roleIds);

    /**
     * 根据用户删除角色关联
     */
    void deleteByUserIds(List<Long> userIds);

    /**
     * 删除用户角色权限缓存
     */
    void deleteCacheByUserIds(Collection<Long> userIds);

}
