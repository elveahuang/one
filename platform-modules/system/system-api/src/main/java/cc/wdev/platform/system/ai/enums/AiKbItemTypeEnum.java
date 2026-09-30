package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiKbItemTypeEnum implements BaseEnum<String> {
    QA("QA", "问答", "问答"),
    TEXT("TEXT", "文本", "文本"),
    DOCUMENT("DOCUMENT", "文档", "文档"),
    NONE("NONE", "未知类型", "未知类型");

    private final String value;
    private final String name;
    private final String description;
}
