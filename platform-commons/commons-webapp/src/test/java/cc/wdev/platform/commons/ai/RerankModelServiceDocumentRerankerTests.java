package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.core.reranker.DefaultDocumentReranker;
import cc.wdev.platform.commons.ai.domain.request.SimpleRerankRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleRerankResponse;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;
import com.alibaba.dashscope.rerank.TextReRankOutput;
import com.alibaba.dashscope.rerank.TextReRankResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 重排服务适配器单元测试
 *
 * @author elvea
 */
public class RerankModelServiceDocumentRerankerTests {

    @Test
    public void shouldReorderByRelevanceScore() {
        RerankModelService service = mock(RerankModelService.class);
        TextReRankResult result = mock(TextReRankResult.class);
        TextReRankOutput output = new TextReRankOutput();
        output.setResults(List.of(
            scored(2, 0.95),
            scored(0, 0.80),
            scored(1, 0.90)
        ));
        when(result.getOutput()).thenReturn(output);
        SimpleRerankResponse<TextReRankResult> response = new SimpleRerankResponse<>();
        response.setResult(result);
        doReturn(response).when(service).call(any(SimpleRerankRequest.class));

        DefaultDocumentReranker reranker = new DefaultDocumentReranker(
            service, SimpleModelConfig.builder().name("gte-rerank-v3").build());

        List<Document> documents = List.of(
            new Document("a", "text-a", Map.of()),
            new Document("b", "text-b", Map.of()),
            new Document("c", "text-c", Map.of())
        );
        List<Document> reranked = reranker.rerank("query", documents);

        Assertions.assertEquals(List.of("text-c", "text-b", "text-a"),
            reranked.stream().map(Document::getText).toList());
        Assertions.assertEquals(0.95, reranked.get(0).getScore());
        Assertions.assertEquals(0.80, reranked.get(2).getScore());
    }

    @Test
    public void shouldFallbackToOriginalOrderOnException() {
        RerankModelService service = mock(RerankModelService.class);
        doThrow(new RuntimeException("boom")).when(service).call(any(SimpleRerankRequest.class));

        DefaultDocumentReranker reranker = new DefaultDocumentReranker(
            service, SimpleModelConfig.builder().name("gte-rerank-v3").build());

        List<Document> documents = List.of(
            new Document("a", "text-a", Map.of()),
            new Document("b", "text-b", Map.of())
        );
        Assertions.assertSame(documents, reranker.rerank("query", documents));
    }

    @Test
    public void shouldReturnOriginalWhenResultUnsupported() {
        RerankModelService service = mock(RerankModelService.class);
        SimpleRerankResponse<Object> response = new SimpleRerankResponse<>();
        response.setResult(new Object());
        doReturn(response).when(service).call(any(SimpleRerankRequest.class));

        DefaultDocumentReranker reranker = new DefaultDocumentReranker(
            service, SimpleModelConfig.builder().name("gte-rerank-v3").build());

        List<Document> documents = List.of(new Document("a", "text-a", Map.of()));
        Assertions.assertSame(documents, reranker.rerank("query", documents));
    }

    private TextReRankOutput.Result scored(int index, double score) {
        TextReRankOutput.Result result = new TextReRankOutput.Result();
        result.setIndex(index);
        result.setRelevanceScore(score);
        return result;
    }

}
