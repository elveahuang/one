package cc.wdev.platform.commons.ai.factory.vectorstore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;

import java.io.Serializable;

import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_VECTOR_STORE_COLLECTION_NAME;

/**
 * @author elvea
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class MariaDBVectorStoreConfig extends VectorStoreConfig implements Serializable {

    private int dimensions = PgVectorStore.INVALID_EMBEDDING_DIMENSION;

    private PgVectorStore.PgIndexType indexType = PgVectorStore.PgIndexType.HNSW;

    private PgVectorStore.PgDistanceType distanceType = PgVectorStore.PgDistanceType.COSINE_DISTANCE;

    private boolean removeExistingVectorStoreTable = false;

    private String tableName = PgVectorStore.DEFAULT_TABLE_NAME;

    private String schemaName = PgVectorStore.DEFAULT_SCHEMA_NAME;

    private PgVectorStore.PgIdType idType = PgVectorStore.PgIdType.UUID;

    private boolean schemaValidation = PgVectorStore.DEFAULT_SCHEMA_VALIDATION;

    private String collectionName = DEFAULT_VECTOR_STORE_COLLECTION_NAME;

    private int maxDocumentBatchSize = PgVectorStore.MAX_DOCUMENT_BATCH_SIZE;

    private boolean initializeSchema = true;

    /**
     * 表名是否附加租户前缀（同一集合名按租户物理隔离；默认关闭，靠元数据过滤隔离）
     */
    private boolean prefixWithTenant = false;

}
