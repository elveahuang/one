package cc.wdev.platform.commons.ai.core.reranker;

import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleRerankRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleRerankResponse;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.alibaba.dashscope.rerank.TextReRankOutput;
import com.alibaba.dashscope.rerank.TextReRankResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
public class DefaultDocumentReranker implements DocumentReranker {

    private final RerankModelService rerankModelService;

    private final ModelConfig modelConfig;

    private final RetrievalConfig retrievalConfig;

    public DefaultDocumentReranker(RerankModelService rerankModelService, ModelConfig modelConfig) {
        this(rerankModelService, modelConfig, RetrievalConfig.builder().build());
    }

    public DefaultDocumentReranker(RerankModelService rerankModelService, ModelConfig modelConfig, RetrievalConfig retrievalConfig) {
        this.rerankModelService = rerankModelService;
        this.modelConfig = modelConfig;
        this.retrievalConfig = retrievalConfig;
    }

    @Override
    public List<Document> rerank(String query, List<Document> documents) {
        if (StringUtils.isEmpty(query) || CollectionUtils.isEmpty(documents)) {
            return documents;
        }

        try {
            List<String> texts = documents.stream().map(Document::getText).toList();
            SimpleRerankRequest request = SimpleRerankRequest.builder()
                .query(query)
                .documents(texts)
                .topN(this.retrievalConfig.getRerankTopN())
                .build();

            SimpleRerankResponse<?> response = this.rerankModelService.call(request);
            if (response == null || !(response.getResult() instanceof TextReRankResult result)
                || result.getOutput() == null || CollectionUtils.isEmpty(result.getOutput().getResults())) {
                return documents;
            }
            return this.applyResult(result.getOutput().getResults(), documents);
        } catch (Exception e) {
            log.warn("Rerank failed, fallback to original order, model={}",
                this.modelConfig != null ? this.modelConfig.getName() : null, e);
            return documents;
        }
    }

    private List<Document> applyResult(List<TextReRankOutput.Result> results, List<Document> documents) {
        List<RerankScore> scores = new ArrayList<>(results.size());
        for (TextReRankOutput.Result result : results) {
            if (result.getIndex() == null) {
                continue;
            }
            scores.add(new RerankScore(result.getIndex(),
                result.getRelevanceScore() != null ? result.getRelevanceScore() : 0d));
        }
        scores.sort(Comparator.comparingDouble(RerankScore::score).reversed());

        List<Document> reranked = new ArrayList<>(scores.size());
        for (RerankScore score : scores) {
            if (score.index >= 0 && score.index < documents.size()) {
                Document document = documents.get(score.index);
                reranked.add(Document.builder()
                    .id(document.getId())
                    .text(document.getText())
                    .metadata(document.getMetadata())
                    .score(score.score)
                    .build());
            }
        }
        return reranked.isEmpty() ? documents : reranked;
    }

    private record RerankScore(int index, double score) {
    }

}
