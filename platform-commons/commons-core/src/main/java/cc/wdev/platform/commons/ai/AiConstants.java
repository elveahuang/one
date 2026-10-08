package cc.wdev.platform.commons.ai;

import org.springframework.ai.session.advisor.SessionMemoryAdvisor;

import java.time.Duration;

/**
 * @author elvea
 */
public interface AiConstants {

    String CHAT_CONTEXT_SESSION_ID = SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY;

    String CHAT_CONTEXT_USER_ID = SessionMemoryAdvisor.USER_ID_CONTEXT_KEY;

    String CHAT_CONTEXT_TENANT_ID = "chat_memory_tenant_id";

    String CHAT_CONTEXT_CHAT_TYPE = "chat_memory_chat_type";

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
        你是一个专业智能助手，你需要根据用户的问题，回答用户的问题。
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
