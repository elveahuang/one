package cc.wdev.platform.commons.ai;

import org.springframework.ai.session.advisor.SessionMemoryAdvisor;

import java.time.Duration;

/**
 * @author elvea
 */
public interface AiConstants {

    String CAHT_CONTEXT_SESSION_ID_KEY = SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY;

    String CAHT_CONTEXT_USER_ID_KEY = SessionMemoryAdvisor.USER_ID_CONTEXT_KEY;

    String CAHT_CONTEXT_TENANT_ID_KEY = "chat_memory_tenant_id";

    /**
     * 默认对话标识，只能用于单元测试，禁止在业务流程中使用
     */
    String DEFAULT_SESSION_ID = "spring-ai-session";

    /**
     * 向量索引
     */
    String DEFAULT_VECTOR_STORE_INDEX_NAME = "vector-store";

    /**
     * 向量索引
     */
    String DEFAULT_VECTOR_STORE_COLLECTION_NAME = "default";

    /**
     * 向量索引
     */
    String DEFAULT_TEST_VECTOR_STORE_COLLECTION_NAME = "test";

    int DEFAULT_CHUNK_LIST_LIMIT = 500;

    /**
     * 默认超时时间
     */
    Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    /**
     * 默认重试次数
     */
    int DEFAULT_MAX_RETRIES = 3;

    /**
     * 系统默认提示词
     */
    String DEFDAULT_PROMPT = """
        你是一个智能体，你需要根据用户的问题，回答用户的问题。
        """;

    /**
     * 结构化输出提示词
     */
    String STRUCTURED_OUTPUT_PROMPT = """
        请严格遵守以下输出协议：
        1. 回复中的所有 JSON 数据必须被包裹在 [[JSON]] 和 [[END]] 之间。
        2. JSON 标记前后可以有普通说明文字，但标记内部只能包含纯净的 JSON 字符串。
        3. 禁止在标记内使用 Markdown 代码块语法（如 ``` ）。
        4. type: string, content: string
        5. content 字段存放json数据
        6. 获取到的职位信息请返回其json数据
        7. 示例格式：
           这里是数据说明：[[JSON]]{"type":"json","content":"{"name":"data"}"}[[END]]。
        """;

    String METADATA_TENANT_ID = "tenantId";

    String METADATA_USER_ID = "userId";

    String METADATA_SESSION_ID = "sessionId";

    String METADATA_CHAT_MEMORY_ID = "chatMemoryId";

    String METADATA_CHAT_TYPE = "chatType";

    String METADATA_AGENT_CODE = "agentCode";

    String DEFAULT_IMAGE_SIZE = "512*512";

    int MAX_MEMORY_MESSAGE_COUNT = 36;

    // ------------------------------------------------------------------------------
    // ReAct Agent
    // ------------------------------------------------------------------------------

    /**
     * 智能体每轮对话允许的最大工具调用总次数（超过后由 ToolCallingManager 中断循环）
     */
    int MAX_AGENT_TOOL_CALLS = 10;

    /**
     * 工具调用事件中参数/结果的最大长度（防止 prompt 爆炸）
     */
    int MAX_AGENT_TOOL_CONTENT_LENGTH = 2000;

    /**
     * 智能体运行状态在 advisor context 中的键
     */
    String AGENT_RUN_STATE_CONTEXT_KEY = "agent_run_state";

    /**
     * 知识库检索工具在 ToolContext 中的知识库ID键
     */
    String TOOL_CONTEXT_KB_ID = "kbId";

    /**
     * 智能体工具使用约定提示词（追加在智能体系统提示词之后）
     */
    String AGENT_REACT_PROMPT = """

        ## 工具使用约定
        1. 你可以调用系统提供的工具来获取实时信息、检索知识或执行操作。
        2. 每次只调用完成当前目标所需的必要工具；能直接回答的问题不要调用工具。
        3. 工具返回结果后，基于结果继续推理；不要编造工具未返回的内容。
        4. 知识库检索工具(search_knowledge_base)用于查询内部知识，回答时优先引用检索到的内容。
        5. 记忆相关操作请静默完成，不要在回复中提及记忆机制本身。
        """;

}
