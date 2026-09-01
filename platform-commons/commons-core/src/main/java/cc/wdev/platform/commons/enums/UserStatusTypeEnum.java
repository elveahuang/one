package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum UserStatusTypeEnum implements BaseEnum<Integer> {
    DELETED(0, "DELETED", "删除状态"),
    OK(1, "OK", "正常状态");

    private final Integer value;
    private final String code;
    private final String description;
}
