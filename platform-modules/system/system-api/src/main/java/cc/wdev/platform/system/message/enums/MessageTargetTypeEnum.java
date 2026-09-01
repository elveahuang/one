package cc.wdev.platform.system.message.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elvea
 */
@Getter
@RequiredArgsConstructor
public enum MessageTargetTypeEnum implements BaseEnum<Integer> {
    IMMEDIATE(1, "IMMEDIATE", "立即发送"),
    FIXED(2, "FIXED", "定时发送"),
    AUTO(3, "AUTO", "跟谁系统");

    private final Integer value;
    private final String code;
    private final String description;
}
