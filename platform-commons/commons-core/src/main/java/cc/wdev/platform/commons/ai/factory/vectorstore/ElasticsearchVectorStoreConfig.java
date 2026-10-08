package cc.wdev.platform.commons.ai.factory.vectorstore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_VECTOR_STORE_COLLECTION_NAME;
import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_VECTOR_STORE_INDEX_NAME;

/**
 * @author elvea
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ElasticsearchVectorStoreConfig extends VectorStoreConfig implements Serializable {

    private String indexName = DEFAULT_VECTOR_STORE_INDEX_NAME;

    private String collectionName = DEFAULT_VECTOR_STORE_COLLECTION_NAME;

    private int dimensions = 1024;

    private String similarity = "cosine";

    private String embeddingFieldName = "embedding";

    private boolean initializeSchema = true;

    /**
     * 索引名是否附加租户前缀
     * 开启时，同一个集合按租户做物理隔离
     * 关闭时，同一个集合靠元数据做逻辑隔离
     */
    private boolean prefixWithTenant = false;

}
