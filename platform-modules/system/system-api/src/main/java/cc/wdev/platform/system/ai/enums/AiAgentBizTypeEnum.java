package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.ai.AiConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiAgentBizTypeEnum implements BaseAiAgentBizTypeEnum {
    DEFAULT("DEFAULT", "智能助手", AiConstants.DEFDAULT_PROMPT, "智能助手"),
    TEST("TEST", "测试助手", AiConstants.DEFDAULT_PROMPT, "测试助手");

    private final String value;
    private final String name;
    private final String prompt;
    private final String description;
}
