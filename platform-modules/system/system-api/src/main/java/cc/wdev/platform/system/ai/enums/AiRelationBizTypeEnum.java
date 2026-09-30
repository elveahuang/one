package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 智能体关联表
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiRelationBizTypeEnum implements BaseEnum<String> {
    AGENT_CURRENT_MODEL("AGENT_CURRENT_MODEL", "智能体<->模型关联"),
    AGENT_CURRENT_TOOL("AGENT_CURRENT_TOOL", "智能体<->工具关联"),
    AGENT_CURRENT_MCP("AGENT_CURRENT_MCP", "智能体<->MCP服务关联"),
    AGENT_CURRENT_KB("AGENT_CURRENT_KB", "智能体<->知识库关联"),
    KB_CURRENT_CHAT_MODEL("KB_CURRENT_CHAT_MODEL", "知识库<->对话模型关联"),
    KB_CURRENT_EMBEDDING_MODEL("KB_CURRENT_EMBEDDING_MODEL", "知识库<->向量模型关联"),
    KB_CURRENT_RERANK_MODEL("KB_CURRENT_RERANK_MODEL", "知识库<->重排模型关联");

    private final String value;
    private final String description;
}
