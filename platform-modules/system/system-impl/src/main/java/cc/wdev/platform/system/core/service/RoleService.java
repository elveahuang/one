package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.form.RoleForm;
import cc.wdev.platform.system.core.domain.request.RoleSearchRequest;
import cc.wdev.platform.system.core.domain.vo.RoleVo;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 * @see EntityService
 */
public interface RoleService extends CachingEntityService<RoleEntity, Long> {

    /**
     * 根据用户ID获取用户所有角色
     *
     * @param userId 用户ID
     * @return 用户所有角色
     */
    List<RoleEntity> findByUserId(Long userId);

    Map<Long, List<RoleEntity>> batchRole(Collection<Long> userIds);

    void saveRole(RoleForm form);

    /**
     * 保存角色权限
     */
    void saveRoleAuthority(RoleForm form);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     *
     * 批量删除角色
     *
     * @param roleIds 角色ID数组
     */
    void batchDeleteRoles(List<Long> roleIds);

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return {@link RoleVo }
     */
    RoleVo findRoleById(Long roleId);

    /**
     * 根据编号或角色名称获取所有角色
     */
    Page<RoleVo> findRolePage(RoleSearchRequest request);

    /**
     * 获取所以角色
     */
    List<RoleVo> findAllRoles();

    /**
     * 获取角色数据权限
     */
    RoleVo getRoleVo(Long roleId);

    /**
     * 获取默认角色
     */
    RoleVo getDefaultRole();
}
