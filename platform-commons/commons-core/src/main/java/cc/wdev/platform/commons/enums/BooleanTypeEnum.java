package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 发布状态类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum BooleanTypeEnum implements BaseEnum<Integer> {
    TRUE(1, Boolean.TRUE, "是"),
    FALSE(0, Boolean.FALSE, "否");

    private final Integer value;
    private final Boolean boolValue;
    private final String description;

    public static BooleanTypeEnum getByValue(Integer value) {
        if (TRUE.getValue().intValue() == value) {
            return TRUE;
        }
        return FALSE;
    }

    public static BooleanTypeEnum getByBoolValue(Boolean boolValue) {
        if (Objects.equals(TRUE.getBoolValue(), boolValue)) {
            return TRUE;
        }
        return FALSE;
    }

    public static int getTrueValue() {
        return TRUE.getValue();
    }

    public static int getFalseValue() {
        return FALSE.getValue();
    }

    public static boolean isTrueValue(Integer value) {
        return Objects.equals(value, TRUE.getValue());
    }

}
