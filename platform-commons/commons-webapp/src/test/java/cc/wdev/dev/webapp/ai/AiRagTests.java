package cc.wdev.dev.webapp.ai;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.tools.CommonTools;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public class AiRagTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private CommonTools commonTools;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.aiManager);

        Map<String, Object> metaMap = Maps.newHashMap();
        metaMap.put("type", "doc");

        Map<String, Object> metaMapA = Maps.newHashMap(metaMap);
        metaMapA.put("docId", 1L);
        metaMapA.put("tenantId", 1L);

        Map<String, Object> metaMapB = Maps.newHashMap(metaMap);
        metaMapB.put("docId", 2L);
        metaMapB.put("tenantId", 1L);

        Map<String, Object> metaMapC = Maps.newHashMap(metaMap);
        metaMapC.put("docId", 3L);
        metaMapC.put("tenantId", 1L);

        List<Document> documents = List.of(
            new Document("1", "教师节是每年的9月10号", metaMapA),
            new Document("2", "劳动节是每年的5月1号", metaMapB),
            new Document("3", "植树节是每年的3月12号", metaMapC)
        );

        VectorStore store = this.aiManager.getVectorStore();
        store.add(documents);

        List<Document> results = store.similaritySearch(SearchRequest.builder()
            .query("教师节")
            .similarityThreshold(0.5)
            .topK(5)
            .build()
        );
        Assertions.assertNotNull(results);
    }

    @Test
    public void baseKbTest() {
        Assertions.assertNotNull(this.aiManager);

        Resource resource = new ClassPathResource("documents/test.md");
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.read();
        if (CollectionUtils.isNotEmpty(documents)) {
            documents.forEach((document) -> document.getMetadata().put("source", "documents/test.md"));
        }

        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.eq("source", "documents/test.md").build();

        VectorStore store = this.aiManager.getVectorStore();
        store.delete(expression);
        store.accept(documents);

        List<Document> results = store.similaritySearch(SearchRequest.builder()
            .query("年假")
            .similarityThreshold(0.5)
            .topK(5)
            .build()
        );
        Assertions.assertNotNull(results);
    }

    /**
     * 基于RetrievalAugmentationAdvisor实现
     */
    @Test
    public void ragChatTest() {
        Assertions.assertNotNull(this.aiManager);

        SessionMemoryAdvisor sessionMemoryAdvisor = this.aiManager.getSessionMemoryAdvisor();
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = this.aiManager.getRetrievalAugmentationAdvisor();

        ChatClient chatClient = ChatClient.builder(this.aiManager.getChatModel())
            .defaultAdvisors(sessionMemoryAdvisor, retrievalAugmentationAdvisor)
            .defaultTools(this.commonTools)
            .build();

        ChatResponse response = chatClient
            .prompt()
            .user("公司的年假天数是怎么规定的")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "spring-ai-session"))
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);

        String text = AiUtils.getChatResponseContent(response);
        Assertions.assertNotNull(text);
    }

    /**
     * 基于QuestionAnswerAdvisor实现
     */
    @Test
    public void questionAnswerAdvisorTest() {
        Assertions.assertNotNull(this.aiManager);

        QuestionAnswerAdvisor questionAnswerAdvisor = this.aiManager.getQuestionAnswerAdvisor();
        SessionMemoryAdvisor sessionMemoryAdvisor = this.aiManager.getSessionMemoryAdvisor();

        ChatClient chatClient = ChatClient.builder(this.aiManager.getChatModel())
            .defaultAdvisors(questionAnswerAdvisor, sessionMemoryAdvisor)
            .defaultTools(this.commonTools)
            .build();

        ChatResponse response = chatClient
            .prompt()
            .user("年假")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "spring-ai-session"))
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);

        String text = AiUtils.getChatResponseContent(response);
        Assertions.assertNotNull(text);
    }

}
