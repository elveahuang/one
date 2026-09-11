package cc.wdev.platform.commons.ai.core.reader;

import cc.wdev.platform.commons.ai.domain.rag.AiDocumentReaderData;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;

import java.util.List;

public interface AiDocumentReader extends DocumentReader {
    /**
     * 获取Document
     */
    List<Document> getReadersToDocuments(List<AiDocumentReaderData> aiDocumentReaders);

    /**
     * 传入documentReaderData
     */
    void setAiDocumentReaders(List<AiDocumentReaderData> aiDocumentReaders);
}
