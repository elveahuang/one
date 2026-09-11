package cc.wdev.platform.commons.ai.core.reader;

import cc.wdev.platform.commons.ai.core.chunk.AiChunkingService;
import cc.wdev.platform.commons.ai.domain.rag.AiDocumentReaderData;
import cc.wdev.platform.commons.ai.domain.response.AiChunkResponse;
import cc.wdev.platform.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultAiDocumentReader extends AiDocumentReaderBase {

    public DefaultAiDocumentReader(AiChunkingService aiChunkingService) {
        super(aiChunkingService);
    }

    @Override
    protected Document toDocument(AiChunkResponse chunk, AiDocumentReaderData reader, Map<String, Object> metadata) {
        PromptTemplate promptTemplate = PromptTemplate.builder().template(KB_SOURCE).variables(metadata).build();
        return Document.builder().text(promptTemplate.render()).metadata(metadata).build();
    }

    @Override
    protected Map<String, Object> toMetadata(AiChunkResponse chunk) {
        try {
            return JacksonUtils.toMap(JacksonUtils.toJson(chunk));
        } catch (Exception e) {
            log.warn("Failed to convert chunk response to metadata", e);
            return Map.of();
        }
    }

    @Override
    public List<Document> get() {
        return getReadersToDocuments(aiDocumentReaders);
    }
}
