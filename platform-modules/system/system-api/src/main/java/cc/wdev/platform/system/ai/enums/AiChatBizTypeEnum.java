package cc.wdev.platform.system.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiChatBizTypeEnum implements BaseAiChatBizTypeEnum {
    DEFAULT("DEFAULT", "默认对话", "默认对话", Boolean.FALSE);

    private final String value;
    private final String name;
    private final String description;
    private final boolean greetingEnabled;

}
