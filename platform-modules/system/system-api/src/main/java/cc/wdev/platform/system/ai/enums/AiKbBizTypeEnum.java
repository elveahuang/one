package cc.wdev.platform.system.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiKbBizTypeEnum implements BaseAiKbBizTypeEnum {
    DEFAULT("DEFAULT", "默认知识库", "默认知识库"),
    TEST("TEST", "测试知识库", "测试知识库");

    private final String value;
    private final String name;
    private final String description;
}
