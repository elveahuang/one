package cc.wdev.platform.commons.ai.factory.vectorstore;

import cc.wdev.platform.commons.ai.enums.AiVectorStoreType;
import cc.wdev.platform.commons.ai.utils.AiRagUtils;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.elasticsearch.SimilarityFunction;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchVectorStoreFactory implements VectorStoreFactory {

    private final ObjectProvider<Rest5Client> restClientProvider;

    private final ObjectProvider<ObservationRegistry> observationRegistry;

    private final ObjectProvider<VectorStoreObservationConvention> convention;

    private final BatchingStrategy batchingStrategy;

    private final ElasticsearchVectorStoreConfig config;

    @Override
    public AiVectorStoreType getStoreType() {
        return AiVectorStoreType.ELASTICSEARCH;
    }

    @Override
    public VectorStore getVectorStore(EmbeddingModel embeddingModel) {
        return this.getVectorStore(embeddingModel, this.config.getCollectionName());
    }

    @Override
    public VectorStore getVectorStore(EmbeddingModel embeddingModel, @NonNull String collectionName) {
        String indexName = this.resolveIndexName(collectionName);
        log.info("Creating ElasticsearchVectorStore with collectionName [{}]. indexName [{}]", collectionName, indexName);

        Rest5Client restClient = this.restClientProvider.getIfAvailable();
        if (restClient == null) {
            throw new IllegalStateException("Rest5Client bean is not available.");
        }

        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();

        PropertyMapper mapper = PropertyMapper.get();
        mapper.from(indexName).whenHasText().to(options::setIndexName);
        mapper.from(this.resolveSimilarity()).to(options::setSimilarity);
        mapper.from(this.config.getDimensions()).to(options::setDimensions);
        mapper.from(this.config.getEmbeddingFieldName()).whenHasText().to(options::setEmbeddingFieldName);

        ElasticsearchVectorStore.Builder builder = ElasticsearchVectorStore.builder(restClient, embeddingModel)
            .options(options)
            .initializeSchema(this.config.isInitializeSchema())
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .customObservationConvention(convention.getIfAvailable())
            .batchingStrategy(batchingStrategy);

        ElasticsearchVectorStore store = builder.build();
        if (this.config.isInitializeSchema()) {
            log.info("Creating ElasticsearchVectorStore index with indexName [{}]", indexName);
            store.afterPropertiesSet();
        }

        return store;
    }

    /**
     * 解析索引名（可选附加租户前缀）
     */
    public String resolveIndexName(String collectionName) {
        if (this.config.isPrefixWithTenant()) {
            return AiRagUtils.resolveIndexName(this.config.getIndexName(), collectionName, TenantContext.getTenantId());
        }
        return AiRagUtils.resolveIndexName(this.config.getIndexName(), collectionName);
    }

    private SimilarityFunction resolveSimilarity() {
        String similarity = this.config.getSimilarity();
        for (SimilarityFunction function : SimilarityFunction.values()) {
            if (function.name().equalsIgnoreCase(similarity)) {
                return function;
            }
        }
        return SimilarityFunction.cosine;
    }

}
