package cc.wdev.platform.system.i18n.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 多语言文本分组
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum LabelGroupTypeEnum implements BaseEnum<String> {
    COMMON("common", "公共类，系统基础模块文本"),
    CUSTOM("custom", "定制类，业务拓展模块文本");

    private final String value;
    private final String description;
}
