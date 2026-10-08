package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum EntityPackageBizTypeEnum implements BaseEnum<String> {
    TENANT("TENANT", "租户-套餐"),
    NONE("NONE", "未指定");

    private final String value;
    private final String description;
}
