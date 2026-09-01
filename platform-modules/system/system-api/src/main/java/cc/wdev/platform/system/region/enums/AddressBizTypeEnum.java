package cc.wdev.platform.system.region.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 地址业务类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AddressBizTypeEnum implements BaseEnum<String> {
    USER("USER", "USER"),
    NONE("NONE", "未指定");

    private final String value;
    private final String description;
}
