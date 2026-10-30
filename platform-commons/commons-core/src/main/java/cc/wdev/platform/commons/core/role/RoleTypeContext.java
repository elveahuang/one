package cc.wdev.platform.commons.core.role;

import cc.wdev.platform.commons.utils.StringUtils;
import org.slf4j.MDC;

/**
 * 当前激活的角色类型上下文。
 *
 * <p>用于「身份切换」：切换后权限计算只取属于该角色类型的角色；
 * 为空表示未切换，按全部角色合并（保持原有行为）。
 * 与 {@link cc.wdev.platform.commons.core.tenant.TenantContext} 一样基于 MDC，
 * 由过滤器在请求进出时设置与清理。</p>
 *
 * @author elvea
 */
public class RoleTypeContext {

    /**
     * 请求头：显式指定本次请求使用的角色类型（多标签页/多端隔离，可选）
     */
    public static final String ROLE_TYPE_HEADER = "x-role-type";

    private static final String ROLE_TYPE = "roleType";

    private RoleTypeContext() {
    }

    public static void setRoleType(String roleType) {
        if (StringUtils.isEmpty(roleType)) {
            clear();
            return;
        }
        MDC.put(ROLE_TYPE, roleType);
    }

    public static String getRoleType() {
        return MDC.get(ROLE_TYPE);
    }

    public static boolean isPresent() {
        return StringUtils.isNotEmpty(MDC.get(ROLE_TYPE));
    }

    public static void clear() {
        MDC.remove(ROLE_TYPE);
    }

}
