package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对话消息内容类型
 */
@Getter
@AllArgsConstructor
public enum AiContentType implements BaseEnum<String> {
    START("[START]", "Start"),
    TEXT("text", "Markdown Text"),
    BLOCK("block", "UI Block"),
    ERROR("error", "Connection timeout."),
    END("[DONE]", "End");

    private final String value;
    private final String description;
}
