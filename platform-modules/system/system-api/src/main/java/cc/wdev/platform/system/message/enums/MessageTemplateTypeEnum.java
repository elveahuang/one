package cc.wdev.platform.system.message.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elvea
 */
@Getter
@RequiredArgsConstructor
public enum MessageTemplateTypeEnum implements BaseEnum<String> {
    JSON("JSON", "JSON"),
    TEXT("TEXT", "TEXT"),
    HTML("HTML", "HTML"),
    TEMPLATE("TEMPLATE", "TEMPLATE"),
    ;
    private final String value;
    private final String description;
}
