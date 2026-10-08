package cc.wdev.platform.commons.ai.core.retriever;

import cc.wdev.platform.commons.ai.core.reranker.DocumentReranker;
import cc.wdev.platform.commons.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

/**
 * 默认检索器
 * 在向量检索器基础上增加重排
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultDocumentRetriever implements DocumentRetriever {

    private final DocumentRetriever delegate;

    private final DocumentReranker reranker;

    @Override
    public @NonNull List<Document> retrieve(@NonNull Query query) {
        List<Document> documents = this.delegate.retrieve(query);
        if (CollectionUtils.isEmpty(documents) || this.reranker == null) {
            return documents;
        }
        String text = query.text();
        try {
            List<Document> reranked = this.reranker.rerank(text, documents);
            return reranked != null ? reranked : documents;
        } catch (Exception e) {
            log.warn("Rerank failed, fallback to original retrieval result", e);
            return documents;
        }
    }

}
