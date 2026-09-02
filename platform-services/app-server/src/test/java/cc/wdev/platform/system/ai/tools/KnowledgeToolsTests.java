package cc.wdev.platform.system.ai.tools;

import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link KnowledgeTools} 单元测试：mock 检索器，不依赖向量库等外部中间件。
 *
 * @author elvea
 */
public class KnowledgeToolsTests {

    @Test
    public void testSearchKnowledgeBase() {
        AiHelper aiHelper = mock(AiHelper.class);
        AiKbVo kb = AiKbVo.builder().id(1L).title("测试知识库").build();
        when(aiHelper.resolveRetrievalConfig(kb)).thenReturn(RetrievalConfig.builder().build());
        when(aiHelper.resolveDocumentRetriever(eq(kb), any()))
            .thenReturn((DocumentRetriever) query -> List.of(
                new Document("d1", "教师节是每年的9月10号。", Map.of("kb_id", 1L)),
                new Document("d2", "劳动节是每年的5月1号。", Map.of("kb_id", 1L))
            ));

        KnowledgeTools tools = new KnowledgeTools(aiHelper, kb);
        String result = tools.searchKnowledgeBase("节日", null);

        assertTrue(result.contains("共检索到 2 条"));
        assertTrue(result.contains("教师节是每年的9月10号"));
        assertTrue(result.contains("劳动节是每年的5月1号"));
    }

    @Test
    public void testSearchKnowledgeBaseEmptyResult() {
        AiHelper aiHelper = mock(AiHelper.class);
        AiKbVo kb = AiKbVo.builder().id(1L).title("测试知识库").build();
        when(aiHelper.resolveRetrievalConfig(kb)).thenReturn(RetrievalConfig.builder().build());
        when(aiHelper.resolveDocumentRetriever(eq(kb), any()))
            .thenReturn((DocumentRetriever) query -> List.of());

        KnowledgeTools tools = new KnowledgeTools(aiHelper, kb);
        String result = tools.searchKnowledgeBase("不存在的内容", 3);

        assertTrue(result.contains("未检索到"));
    }

}
