package cc.wdev.platform.system.config.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统配置项类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ConfigContentTypeEnum implements BaseEnum<Integer> {
    TEXT(1, "TEXT", "单行文本"),
    TEXTAREA(2, "TEXTAREA", "多行文本"),
    RICHTEXT(3, "RICHTEXT", "富文本"),
    DATETIME(4, "DATETIME", "日期时间"),
    DATE(5, "DATE", "日期"),
    TIME(6, "TIME", "时间"),
    PAGE(7, "PAGE", "页面"),
    PASSWORD(8, "PASSWORD", "密码"),
    BOOL(9, "BOOL", "布尔值"),
    ;

    private final Integer value;
    private final String code;
    private final String description;
}
