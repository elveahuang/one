package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiGreetingTemplateEnum implements BaseEnum<String> {
    GREETING_DEFAULT_TEMPLATE("defaultGreeting", "默认开场白");

    private final String value;
    private final String description;
}
