package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对话消息响应类型
 */
@Getter
@AllArgsConstructor
public enum AiResponseType implements BaseEnum<String> {
    TEXT("TEXT", "普通文本"),
    JSON("JSON", "JSON文本");

    private final String value;
    private final String description;
}
