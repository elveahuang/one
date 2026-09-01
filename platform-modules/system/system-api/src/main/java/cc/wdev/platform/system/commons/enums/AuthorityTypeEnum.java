package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AuthorityTypeEnum implements BaseEnum<Integer> {
    MODULE(1, "MODULE", "模块"),
    GROUP(2, "GROUP", "分组"),
    RESOURCE(3, "RESOURCE", "资源"),
    PERMISSION(4, "PERMISSION", "权限");

    private final Integer value;
    private final String code;
    private final String description;
}
