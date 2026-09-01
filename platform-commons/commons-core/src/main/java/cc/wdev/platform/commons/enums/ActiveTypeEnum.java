package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启用状态类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ActiveTypeEnum implements BaseEnum<Integer> {
    ENABLED(1, "启用，正常状态"),
    DISABLED(0, "禁用，删除状态");

    private final Integer value;
    private final String description;

    public static Integer getEnabledValue() {
        return ActiveTypeEnum.ENABLED.getValue();
    }

    public static Integer getDisabledValue() {
        return ActiveTypeEnum.DISABLED.getValue();
    }

}
