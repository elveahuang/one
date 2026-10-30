package cc.wdev.platform.commons.core.identity;

import cc.wdev.platform.commons.utils.StringUtils;
import org.slf4j.MDC;

/**
 * 当前激活的身份端上下文。
 *
 * <p>用于「身份切换」：用户在同一时刻只处于一个端（管理端 / 会员端），
 * 切换后权限计算只取属于该端的角色类型；为空表示未切换，按全部角色合并（保持原有行为）。
 * 与 {@link cc.wdev.platform.commons.core.tenant.TenantContext} 一样基于 MDC，
 * 由过滤器在请求进出时设置与清理。</p>
 *
 * @author elvea
 */
public class IdentitySideContext {

    /**
     * 请求头：显式指定本次请求使用的身份端（多标签页/多端隔离，可选）
     */
    public static final String IDENTITY_SIDE_HEADER = "x-identity-side";

    private static final String IDENTITY_SIDE = "identitySide";

    private IdentitySideContext() {
    }

    public static void setIdentitySide(String identitySide) {
        if (StringUtils.isEmpty(identitySide)) {
            clear();
            return;
        }
        MDC.put(IDENTITY_SIDE, identitySide);
    }

    public static String getIdentitySide() {
        return MDC.get(IDENTITY_SIDE);
    }

    public static boolean isPresent() {
        return StringUtils.isNotEmpty(MDC.get(IDENTITY_SIDE));
    }

    public static void clear() {
        MDC.remove(IDENTITY_SIDE);
    }

}
