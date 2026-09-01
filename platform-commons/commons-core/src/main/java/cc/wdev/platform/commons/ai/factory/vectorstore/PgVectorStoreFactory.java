package cc.wdev.platform.commons.ai.factory.vectorstore;

import cc.wdev.platform.commons.ai.enums.AiVectorStoreType;
import cc.wdev.platform.commons.ai.utils.AiRagUtils;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class PgVectorStoreFactory implements VectorStoreFactory {

    private final ObjectProvider<JdbcTemplate> jdbcTemplateProvider;
    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<VectorStoreObservationConvention> customObservationConvention;
    private final BatchingStrategy batchingStrategy;
    private final PgVectorStoreConfig config;

    @Override
    public AiVectorStoreType getStoreType() {
        return AiVectorStoreType.PGVECTOR;
    }

    @Override
    public VectorStore getVectorStore(EmbeddingModel embeddingModel) {
        return this.getVectorStore(embeddingModel, this.config.getCollectionName());
    }

    @Override
    public VectorStore getVectorStore(EmbeddingModel embeddingModel, @NonNull String collectionName) {
        JdbcTemplate jdbcTemplate = this.jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            throw new IllegalStateException("JdbcTemplate bean is not available.");
        }
        String tableName = this.config.isPrefixWithTenant()
            ? AiRagUtils.resolveTableName(this.config.getTableName(), collectionName, TenantContext.getTenantId())
            : AiRagUtils.resolveTableName(this.config.getTableName(), collectionName);
        log.info("Creating PgVectorStore with collectionName [{}]. tableName [{}]", collectionName, tableName);

        PgVectorStore.PgVectorStoreBuilder builder = PgVectorStore.builder(jdbcTemplate, embeddingModel)
            .schemaName(this.config.getSchemaName())
            .idType(this.config.getIdType())
            .vectorTableName(tableName)
            .vectorTableValidationsEnabled(this.config.isSchemaValidation())
            .dimensions(this.config.getDimensions())
            .distanceType(this.config.getDistanceType())
            .removeExistingVectorStoreTable(this.config.isRemoveExistingVectorStoreTable())
            .indexType(this.config.getIndexType())
            .initializeSchema(this.config.isInitializeSchema())
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .customObservationConvention(customObservationConvention.getIfAvailable())
            .batchingStrategy(batchingStrategy)
            .maxDocumentBatchSize(this.config.getMaxDocumentBatchSize());

        // 不经过容器创建索引，需要手工触发一次
        PgVectorStore vectorStore = builder.build();
        if (this.config.isInitializeSchema()) {
            vectorStore.afterPropertiesSet();
        }

        return vectorStore;
    }

}
