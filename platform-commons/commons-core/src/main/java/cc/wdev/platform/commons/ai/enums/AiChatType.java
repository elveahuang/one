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
public enum AiChatType implements BaseEnum<String> {
    CHAT("CHAT", "文本对话", "文本对话"),
    AGENT("AGENT", "智能体对话", "智能体对话"),
    KB("KB", "知识库对话", "知识库对话"),
    STATIC("STATIC", "静态对话", "静态对话"),
    NONE("NONE", "未知对话类型", "未知对话类型");

    private final String value;
    private final String name;
    private final String description;
}
