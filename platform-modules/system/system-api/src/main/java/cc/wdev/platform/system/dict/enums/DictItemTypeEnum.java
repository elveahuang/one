package cc.wdev.platform.system.dict.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统默认初始的字典数据
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum DictItemTypeEnum implements BaseDictItemTypeEnum {
    ;

    private final String code;
    private final String title;
    private final String type;
    private final Integer idx;
    private final String description;
}
