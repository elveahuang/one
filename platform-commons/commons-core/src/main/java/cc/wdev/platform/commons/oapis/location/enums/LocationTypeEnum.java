package cc.wdev.platform.commons.oapis.location.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationTypeEnum implements BaseEnum<String> {
    Tianditu("Tianditu", "天地图"),
    None("None", "None");

    private final String value;
    private final String description;
}
