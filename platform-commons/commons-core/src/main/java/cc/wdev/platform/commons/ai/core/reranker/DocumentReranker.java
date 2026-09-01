package cc.wdev.platform.commons.ai.core.reranker;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * 文档重排器
 * 对召回结果按查询相关性重新排序
 *
 * @author elvea
 */
public interface DocumentReranker {

    /**
     * 对召回文档执行重排
     *
     * @param query     原始查询
     * @param documents 召回文档（按原顺序）
     * @return 重排后的文档；实现无法处理时必须原样返回，不得抛异常
     */
    List<Document> rerank(String query, List<Document> documents);

}
