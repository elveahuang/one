package cc.wdev.platform.system.i18n.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum LabelTypeEnum implements BaseEnum<String> {
    PROPERTIES("properties", "messages_%s", "properties"),
    JSON("json", "%s", "json");

    private final String value;
    private final String filename;
    private final String description;
}
