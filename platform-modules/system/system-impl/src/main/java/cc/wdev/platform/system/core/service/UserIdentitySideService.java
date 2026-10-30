package cc.wdev.platform.system.core.service;

import cc.wdev.platform.system.core.domain.vo.UserIdentitySideVo;

/**
 * 用户身份端切换服务。
 *
 * <p>身份被划分为两个「端」：</p>
 * <ul>
 *   <li><b>管理端</b>（{@code ADMIN}）：平台管理员 / 系统管理员 / 普通用户，权限为三者并集</li>
 *   <li><b>会员端</b>（{@code MEMBER}）：仅会员角色</li>
 * </ul>
 *
 * <p>用户在同一时刻只处于其中一端，切换即在两端之间切换。拥有两端身份的用户才需要切换；
 * 只属于一端的用户不需要（也没有可选目标）。</p>
 *
 * @author elvea
 */
public interface UserIdentitySideService {

    /**
     * 查询用户可切换的身份端信息（当前端 + 可用端）。
     */
    UserIdentitySideVo getIdentitySide(Long userId);

    /**
     * 切换当前身份端。
     *
     * @param userId 用户ID
     * @param side   目标端（ADMIN / MEMBER）
     * @return 切换后的身份端信息
     */
    UserIdentitySideVo switchIdentitySide(Long userId, String side);

    /**
     * 解析本次请求真正生效的身份端。
     *
     * <p>把「请求声明的端」与「服务端记录的端」统一收口到这里，供权限计算使用。
     * 只接受该用户真实拥有的端，任何伪造值都会被丢弃；返回 null 表示不做端过滤
     * （按全部角色合并，用于只属于单一端的用户）。</p>
     *
     * @param userId               用户ID
     * @param requestedIdentitySide 请求声明的端（如请求头），可为空
     * @return 生效的端；null 表示不过滤
     */
    String resolveEffectiveIdentitySide(Long userId, String requestedIdentitySide);

    /**
     * 清理用户当前身份端记录（登出时调用）。
     */
    void clearIdentitySide(Long userId);

}
