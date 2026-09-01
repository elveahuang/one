package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 套餐类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum PackageBizTypeEnum implements BaseEnum<String> {
    TENANT("TENANT", "租户套餐"),
    MEMBER("MEMBER", "会员套餐");

    private final String value;
    private final String description;
}
