package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiApiKeyBizTypeEnum implements BaseEnum<String> {
    MCP_API_KEY("MCP_API_KEY", "sk-", "MCP API Key");

    private final String value;
    private final String prefix;
    private final String description;
}
