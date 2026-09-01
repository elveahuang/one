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

}
