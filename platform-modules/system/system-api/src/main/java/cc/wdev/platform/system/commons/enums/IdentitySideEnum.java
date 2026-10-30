package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 身份端。
 *
 * <p>把 {@link RoleTypeEnum} 的四种角色类型收敛为两个「端」，用于身份切换：</p>
 * <ul>
 *   <li>{@link #ADMIN}（管理端）：{@code PLATFORM_ADMINISTRATOR}、{@code SYSTEM_ADMINISTRATOR}、{@code USER}
 *       三类角色类型的权限<b>并集</b></li>
 *   <li>{@link #MEMBER}（会员端）：仅 {@code MEMBER}</li>
 * </ul>
 *
 * <p>用户在同一时刻只处于其中一端，切换是指在两端之间切换，端内不再细分。</p>
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum IdentitySideEnum implements BaseEnum<String> {
    ADMIN("ADMIN", "管理端", List.of(
        RoleTypeEnum.PLATFORM_ADMINISTRATOR.getValue(),
        RoleTypeEnum.SYSTEM_ADMINISTRATOR.getValue(),
        RoleTypeEnum.USER.getValue()
    )),
    MEMBER("MEMBER", "会员端", List.of(
        RoleTypeEnum.MEMBER.getValue()
    ));

    private final String value;
    private final String description;

    /**
     * 该端包含的角色类型（sys_role.biz_type）
     */
    private final List<String> roleTypes;

    public String getLabelKey() {
        return ("label__identity_side__" + getValue()).toLowerCase();
    }

    /**
     * 把角色类型解析为所属的端；未知类型返回 null。
     */
    public static IdentitySideEnum ofRoleType(String roleType) {
        if (roleType == null || roleType.isBlank()) {
            return null;
        }
        for (IdentitySideEnum side : values()) {
            for (String type : side.getRoleTypes()) {
                if (type.equalsIgnoreCase(roleType.trim())) {
                    return side;
                }
            }
        }
        return null;
    }

    /**
     * 按值解析；未知值返回 null。
     */
    public static IdentitySideEnum of(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (IdentitySideEnum side : values()) {
            if (side.getValue().equalsIgnoreCase(value.trim())) {
                return side;
            }
        }
        return null;
    }

}
