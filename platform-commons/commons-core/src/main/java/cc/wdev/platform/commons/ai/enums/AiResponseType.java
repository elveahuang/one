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
    JSON("JSON", "JSON文本"),
    STRICT("STRICT", "严格模式，全部响应数据做结构化输出，保证数据准确性");

    private final String value;
    private final String description;
}
