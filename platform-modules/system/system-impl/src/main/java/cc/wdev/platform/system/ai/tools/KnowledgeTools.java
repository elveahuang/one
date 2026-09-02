package cc.wdev.platform.system.ai.tools;

import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 知识库检索工具（Agentic RAG）
 * <p>
 * 把知识库检索暴露为模型可自主调用的工具，与预检索 Advisor（RetrievalAugmentationAdvisor）互补：
 * 简单问题由预检索兜底，复杂/多跳问题由模型在 ReAct 循环中按需多次检索。
 * <p>
 * 实例与智能体绑定的知识库一一对应，检索内部走
 * {@link AiHelper#resolveDocumentRetriever(AiKbVo, RetrievalConfig)}（租户过滤 + 重排）。
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class KnowledgeTools {

    private static final int MAX_DOCUMENT_TEXT_LENGTH = 1000;

    private final AiHelper aiHelper;

    private final AiKbVo kb;

    @Tool(name = "search_knowledge_base", description = "在当前智能体绑定的知识库中检索与查询语句最相关的知识片段，适用于需要查询内部资料、文档、产品说明、常见问题等场景")
    public String searchKnowledgeBase(@ToolParam(description = "用于检索的问题或关键词") @NonNull String query,
                                      @ToolParam(required = false, description = "期望返回的知识条数，不传则使用系统默认值") Integer topK) {
        try {
            RetrievalConfig config = this.aiHelper.resolveRetrievalConfig(this.kb);
            if (topK != null && topK > 0 && topK != config.getTopK()) {
                config = RetrievalConfig.builder()
                    .retrieverType(config.getRetrieverType())
                    .topK(topK)
                    .similarityThreshold(config.getSimilarityThreshold())
                    .rerankEnabled(config.isRerankEnabled())
                    .rerankTopN(config.getRerankTopN())
                    .build();
            }

            DocumentRetriever retriever = this.aiHelper.resolveDocumentRetriever(this.kb, config);
            List<Document> documents = retriever.retrieve(new Query(query));
            if (documents == null || documents.isEmpty()) {
                return "知识库中未检索到与查询相关的内容。";
            }

            StringBuilder builder = new StringBuilder();
            builder.append("共检索到 ").append(documents.size()).append(" 条相关知识：\n");
            int index = 1;
            for (Document document : documents) {
                String text = truncate(document.getText());
                builder.append("\n[").append(index++).append("] ").append(text).append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            log.error("searchKnowledgeBase [{}] error. query: {}", this.kb.getId(), query, e);
            return "知识库检索失败，请稍后重试。";
        }
    }

    private static String truncate(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        return text.length() > MAX_DOCUMENT_TEXT_LENGTH ? text.substring(0, MAX_DOCUMENT_TEXT_LENGTH) + "…" : text;
    }

}
