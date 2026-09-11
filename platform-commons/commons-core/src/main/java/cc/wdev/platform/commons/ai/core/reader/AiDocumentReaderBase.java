package cc.wdev.platform.commons.ai.core.reader;

import cc.wdev.platform.commons.ai.core.chunk.AiChunkingService;
import cc.wdev.platform.commons.ai.domain.rag.AiDocumentReaderData;
import cc.wdev.platform.commons.ai.domain.response.AiChunkResponse;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AiDocumentReaderBase implements AiDocumentReader {
    /**
     * 音视频资源
     */
    protected static final List<String> VIDEO_SOURCES = List.of("VIDEO", "AUDIO");

    protected static final String KB_SOURCE = """
        {sectionContent}

        【知识来源】
        章节标题：{sectionTitle}
        页码：{pageNo}
        开始时间戳（ms）：{startTime}
        结束时间戳（ms）：{endTime} \n
        """;

    protected final ExtractedTextFormatter textFormatter;
    protected final AiChunkingService aiChunkingService;
    protected List<AiDocumentReaderData> aiDocumentReaders;
    protected AiDocumentReaderBase(AiChunkingService aiChunkingService) {
        this(aiChunkingService, List.of());
    }

    protected AiDocumentReaderBase(AiChunkingService aiChunkingService, List<AiDocumentReaderData> readers) {
        this(ExtractedTextFormatter.defaults(), aiChunkingService, readers);
    }

    protected AiDocumentReaderBase(ExtractedTextFormatter textFormatter, AiChunkingService aiChunkingService, List<AiDocumentReaderData> aiDocumentReaders) {
        this.textFormatter = textFormatter;
        this.aiChunkingService = aiChunkingService;
        this.aiDocumentReaders = aiDocumentReaders;
    }

    /**
     * 传入documentReaderData
     */
    @Override
    public void setAiDocumentReaders(List<AiDocumentReaderData> aiDocumentReaders) {
        this.aiDocumentReaders = aiDocumentReaders;
    }

    /**
     * 固定处理流程
     */
    @Override
    public final List<Document> getReadersToDocuments(List<AiDocumentReaderData> readers) {
        List<Document> documents = Lists.newArrayListWithExpectedSize(readers.size());

        if (CollectionUtils.isEmpty(readers)) {
            return documents;
        }

        for (AiDocumentReaderData reader : readers) {
            List<AiChunkResponse> chunks = getChunkVos(reader.getText(), reader.getType());
            if (CollectionUtils.isEmpty(chunks)) {
                continue;
            }
            for (AiChunkResponse chunk : chunks) {
                Map<String, Object> metadata = toMetadata(chunk);
                if (CollectionUtils.isNotEmpty(reader.getMetadata())) {
                    metadata.putAll(reader.getMetadata());
                }
                Document document = toDocument(chunk, reader, metadata);
                if (null != document) {
                    document.getMetadata().remove("sectionContent");
                    documents.add(document);
                }
            }
        }
        return documents;
    }

    /**
     * Chunk 策略
     */
    protected List<AiChunkResponse> getChunkVos(String content, String sourceType) {
        if (StringUtils.isNotEmpty(sourceType) && VIDEO_SOURCES.contains(sourceType)) {
            return aiChunkingService.chunkVideo(content);
        }
        return aiChunkingService.chunkDocument(content);
    }

    /**
     * Metadata 转换策略
     */
    protected abstract Map<String, Object> toMetadata(AiChunkResponse chunk);

    /**
     * Document 转换策略
     */
    protected abstract Document toDocument(AiChunkResponse chunk, AiDocumentReaderData reader, Map<String, Object> metadata);
}
