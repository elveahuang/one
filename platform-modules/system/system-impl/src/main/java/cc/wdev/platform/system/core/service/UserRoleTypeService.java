package cc.wdev.platform.system.core.service;

import cc.wdev.platform.system.core.domain.vo.UserRoleTypeVo;

/**
 * 用户身份（角色类型）切换服务。
 *
 * <p>「身份」指 {@code RoleTypeEnum} 中的一个角色类型：
 * {@code PLATFORM_ADMINISTRATOR} / {@code SYSTEM_ADMINISTRATOR} / {@code USER} / {@code MEMBER}。
 * 切换后，权限计算只取属于该类型的角色。</p>
 *
 * @author elvea
 */
public interface UserRoleTypeService {

    /**
     * 查询用户可切换的身份信息（当前身份 + 可用身份）。
     */
    UserRoleTypeVo getRoleType(Long userId);

    /**
     * 切换当前身份。
     *
     * @param userId   用户ID
     * @param roleType 目标角色类型
     * @return 切换后的身份信息
     */
    UserRoleTypeVo switchRoleType(Long userId, String roleType);

    /**
     * 读取用户当前身份；未切换或记录的角色类型已不再拥有时返回 null。
     */
    String getCurrentRoleType(Long userId);

    /**
     * 清理用户当前身份记录（登出时调用）。
     */
    void clearRoleType(Long userId);

}
