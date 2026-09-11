package cc.wdev.platform.commons.ai.core.chunk;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.response.AiChunkResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 默认的资源分块服务实现
 */
@Slf4j
@Builder
@Service
public class DefaultAiChunkingService implements AiChunkingService {

    private final AiManager aiManager;

    public DefaultAiChunkingService(AiManager aiManager) {
        Assert.notNull(aiManager, "aiManager must not be null");
        this.aiManager = aiManager;
    }

    @Override
    public List<AiChunkResponse> chunkVideo(String transcript) {
        Assert.notNull(aiManager, "aiManager must not be null");
        return aiManager.getChatClient().prompt()
            .system("""
                你是企业学习平台的视频知识切片专家。

                请根据视频内容的语义进行切片。

                要求：
                1. 一个 Chunk 只表达一个完整知识主题
                2. 不要在一句话中间切断
                3. 不要丢失上下文
                4. 每个 Chunk 建议 300~400 字
                5. 必须返回 startTime 和 endTime
                6. 不要修改原始内容
                """)
            .user(transcript)
            .call()
            .entity(new ParameterizedTypeReference<List<AiChunkResponse>>() {
            }, ChatClient.EntityParamSpec::validateSchema);
    }

    @Override
    public List<AiChunkResponse> chunkDocument(String content) {
        Assert.notNull(aiManager, "aiManager must not be null");
        return aiManager.getChatClient().prompt()
            .system("""
                你是企业学习平台的文档知识切片专家。

                请按照语义和文档结构进行切片。

                要求：
                1. 一个 Chunk 表达一个完整知识主题
                2. 保留标题与上下文
                3. 不要破坏代码块
                4. 不要在一句话中间切割
                5. 每个 Chunk 建议 300~400 字
                6. 无开始结束时间
                """)
            .user(content)
            .call()
            .entity(new ParameterizedTypeReference<List<AiChunkResponse>>() {
            }, ChatClient.EntityParamSpec::validateSchema);
    }

    @Override
    public List<AiChunkResponse> chunkExam(String content) {
        Assert.notNull(aiManager, "aiManager must not be null");
        return aiManager.getChatClient().prompt()
            .system("""
                你是企业学习平台的考试知识结构化专家。

                请按照题目进行切片。

                要求：
                1. 一道题必须作为一个完整 Chunk
                2. 必须保留题目、选项、答案、解析
                3. 不允许把一道题拆成多个 Chunk
                """)
            .user(content)
            .call()
            .entity(new ParameterizedTypeReference<List<AiChunkResponse>>() {
            }, ChatClient.EntityParamSpec::validateSchema);
    }
}
