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
     * 解析本次请求真正生效的角色类型。
     *
     * <p>把「请求声明的类型」与「服务端记录的类型」统一收口到这里，供权限计算使用。
     * 只接受该用户真实拥有的类型，任何伪造值都会被丢弃；返回 null 表示不做类型过滤
     * （按全部角色合并，用于从未切换过身份的用户）。</p>
     *
     * @param userId          用户ID
     * @param requestedRoleType 请求声明的类型（如请求头），可为空
     * @return 生效的角色类型；null 表示不过滤
     */
    String resolveEffectiveRoleType(Long userId, String requestedRoleType);

    /**
     * 清理用户当前身份记录（登出时调用）。
     */
    void clearRoleType(Long userId);

}
