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
    START("[START]", "开始标记"),
    TEXT("text", "文本"),
    CITATION("citation", "引用"),
    INTERACTION("interaction", "交互"),
    THOUGHT("thought", "智能体思考"),
    TOOL_CALL("tool_call", "工具调用"),
    TOOL_RESULT("tool_result", "工具结果"),
    ERROR("error", "Connection timeout."),
    END("[DONE]", "结束标记");

    private final String value;
    private final String description;
}
