package cc.wdev.platform.commons.ai.core.chunk;

import cc.wdev.platform.commons.ai.domain.response.AiChunkResponse;

import java.util.List;

/**
 * 文档分块服务
 */
public interface AiChunkingService {

    List<AiChunkResponse> chunkVideo(String transcript);

    List<AiChunkResponse> chunkDocument(String content);

    List<AiChunkResponse> chunkExam(String content);
}
