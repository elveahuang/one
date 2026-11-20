package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对话业务类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiChatBizType implements BaseEnum<String> {
    DEFAULT("DEFAULT", "智能助手", "智能助手");

    private final String value;
    private final String name;
    private final String description;
}
