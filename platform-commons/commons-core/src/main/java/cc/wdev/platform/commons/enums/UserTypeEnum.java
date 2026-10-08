package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum implements BaseEnum<String> {
    USER("USER", "用户"),
    ACCOUNT("ACCOUNT", "账号");

    private final String value;
    private final String description;
}
