package cc.wdev.platform.system.ai.helpers;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.ai.config.SplittingConfig;
import cc.wdev.platform.commons.ai.config.VectorizationConfig;
import cc.wdev.platform.commons.ai.core.reranker.DefaultDocumentReranker;
import cc.wdev.platform.commons.ai.core.reranker.DocumentReranker;
import cc.wdev.platform.commons.ai.core.retriever.DefaultDocumentRetriever;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.NumberUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiRelationEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbMetadataRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.enums.AiRelationBizTypeEnum;
import cc.wdev.platform.system.ai.service.AiModelService;
import cc.wdev.platform.system.ai.service.AiRelationService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.AI_INVALID_KB_MODEL;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * 封装系统模块和公共模块集成的通用方法
 *
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiHelper {

    public static final String METADATA_TENANT_ID = "tenant_id";

    public static final String METADATA_KB_ID = "kb_id";

    public static final String METADATA_KB_ITEM_ID = "kb_item_id";

    public static final String METADATA_KB_ITEM_TYPE = "kb_item_type";

    public static final String METADATA_KB_ITEM_BIZ_TYPE = "kb_item_biz_type";

    public static final String METADATA_KB_ITEM_BIZ_ID = "kb_item_biz_id";

    public static final String METADATA_KB_CHUNK_ID = "kb_chunk_id";

    public static final String METADATA_KB_CHUNK_INDEX = "kb_chunk_index";

    public static final String METADATA_KB_CHUNK_START_INDEX = "kb_chunk_start_index";

    public static final String METADATA_KB_CHUNK_END_INDEX = "kb_chunk_end_index";

    private final AiManager aiManager;

    private final AiServiceManager aiServiceManager;

    private final AiModelService aiModelService;

    private final AiRelationService aiRelationService;

    /**
     * 获取当前租户ID
     */
    public Long resolveTenant(@NonNull AiKbVo kb) {
        return nvl(kb.getTenantId(), TenantContext.getTenantId());
    }

    /**
     * 获取向量文档ID
     */
    public String resolveDocId(@NonNull Long kbId, @NonNull String contentHash) {
        return "kb-" + NumberUtils.nvl(kbId) + "-" + StringUtils.nvl(contentHash);
    }

    /**
     * 获取知识库对应的向量存储
     */
    public VectorStore resolveVectorStore(@NonNull AiKbVo kb) {
        ModelConfig modelConfig = this.resolveModelConfig(kb.getEmbeddingModel());
        return this.aiManager.getVectorStore(modelConfig, kb.getCollectionName());
    }

    /**
     * 批量向量化并写入向量库
     */
    public void vectorize(@NonNull VectorStore vectorStore, @NonNull List<Document> documents) {
        VectorizationConfig config = this.resolveVectorizationConfig();
        List<List<Document>> list = Lists.partition(documents, config.getBatchSize());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(vectorStore::add);
        }
    }

    /**
     * 获取知识库对应的文档检索器
     */
    public DocumentRetriever resolveDocumentRetriever(@NonNull AiKbVo kb) {
        Filter.Expression filter = this.buildScopeFilter(kb);
        RetrievalConfig config = this.resolveRetrievalConfig(kb);
        DocumentRetriever retriever = AiUtils.getDocumentRetriever(this.resolveVectorStore(kb), config, filter);
        DocumentReranker reranker = this.resolveDocumentReranker(kb, config);
        if (reranker != null) {
            retriever = new DefaultDocumentRetriever(retriever, reranker);
        }
        return retriever;
    }

    /**
     * 获取知识库对应的文档重排器
     */
    public DocumentReranker resolveDocumentReranker(@NonNull AiKbVo kb, RetrievalConfig retrievalConfig) {
        // 默认配置重排模型
        if (ObjectUtils.isValidId(kb.getRerankModelId())) {
            ModelConfig modelConfig = this.resolveModelConfig(kb.getRerankModel());
            RerankModelService service = this.aiServiceManager.getRerankModelService(modelConfig);
            return new DefaultDocumentReranker(service, modelConfig, retrievalConfig);
        }
        return null;
    }

    /**
     * 获取知识库文档检索器
     */
    public RetrievalAugmentationAdvisor resolveRetrievalAugmentationAdvisor(AiKbVo kb) {
        DocumentRetriever retriever = this.resolveDocumentRetriever(kb);
        return RetrievalAugmentationAdvisor.builder()
            .documentRetriever(retriever)
            .queryAugmenter(ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build()
            ).build();
    }

    /**
     * 构建知识库检索过滤条件
     * 1. 租户隔离
     */
    public Filter.Expression buildScopeFilter(@NonNull AiKbVo kb) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        FilterExpressionBuilder.Op op = builder.eq(METADATA_TENANT_ID, resolveTenant(kb));
        return op.build();
    }

    /**
     * 注入元数据
     */
    public void applyDocumentMetadata(List<Document> documents, Map<String, Object> metadata) {
        documents.stream().filter(Objects::nonNull).forEach(document -> metadata.forEach((k, v) -> {
            if (!document.getMetadata().containsKey(k)) {
                document.getMetadata().put(k, v);
            }
        }));
    }

    /**
     * 构建元数据
     */
    public Map<String, Object> buildDocumentMetadata(@NonNull AiKbMetadataRequest request,
                                                     Map<String, Object> extra) {
        Map<String, Object> metadata = new HashMap<>();
        if (CollectionUtils.isNotEmpty(extra)) {
            metadata.putAll(extra);
        }
        if (ObjectUtils.isValidId(request.getTenantId())) {
            metadata.put(METADATA_TENANT_ID, request.getTenantId());
        }
        if (ObjectUtils.isValidId(request.getKbId())) {
            metadata.put(METADATA_KB_ID, request.getKbId());
        }
        if (ObjectUtils.isValidId(request.getKbItemId())) {
            metadata.put(METADATA_KB_ITEM_ID, request.getKbItemId());
        }
        if (ObjectUtils.isValidId(request.getKbItemType())) {
            metadata.put(METADATA_KB_ITEM_TYPE, request.getKbItemType());
        }
        if (ObjectUtils.isValidId(request.getKbItemBizType())) {
            metadata.put(METADATA_KB_ITEM_BIZ_TYPE, request.getKbItemBizType());
        }
        if (ObjectUtils.isValidId(request.getKbItemBizId())) {
            metadata.put(METADATA_KB_ITEM_BIZ_ID, request.getKbItemBizId());
        }
        if (ObjectUtils.isValidId(request.getKbChunkId())) {
            metadata.put(METADATA_KB_CHUNK_ID, request.getKbChunkId());
        }
        if (ObjectUtils.isValidId(request.getKbChunkId())) {
            metadata.put(METADATA_KB_CHUNK_INDEX, request.getKbChunkId());
        }
        return metadata;
    }

    /**
     * 获取模型配置
     */
    public ModelConfig resolveModelConfig(@NonNull AiModelVo model) {
        return SimpleModelConfig.builder()
            .name(model.getModelName())
            .modelType(model.getModelType())
            .serviceProvider(model.getServiceProvider())
            .modelProvider(model.getModelProvider())
            .baseUrl(model.getBaseUrl())
            .apiKey(model.getApiKey())
            .build();
    }

    /**
     * 获取知识库向量存储配置
     */
    public VectorizationConfig resolveVectorizationConfig() {
        return this.aiManager.getConfig().getVectorization();
    }

    /**
     * 获取知识库检索配置
     */
    public RetrievalConfig resolveRetrievalConfig(@NonNull AiKbVo kb) {
        RetrievalConfig globalConfig = this.aiManager.getConfig().getRetrieval();
        return AiUtils.resolveRetrievalConfig(globalConfig, RetrievalConfig.builder()
            .build());
    }

    /**
     * 获取知识库切片配置
     */
    public SplittingConfig resolveSplittingConfig(@NonNull AiKbVo kb) {
        SplittingConfig.SplittingConfigBuilder builder = SplittingConfig.builder();
        if (kb.getChunkSize() != null) {
            builder.chunkSize(kb.getChunkSize());
        }
        if (kb.getChunkOverlap() != null) {
            builder.chunkOverlap(kb.getChunkOverlap());
        }
        SplittingConfig globalConfig = this.aiManager.getConfig().getSplitting();
        return AiUtils.resolveSplittingConfig(globalConfig, builder.build());
    }

    /**
     * 获取知识库附加信息
     */
    public void getAiKbExtra(@NonNull AiKbVo aiKbVo) {
        if (ObjectUtils.isValidId(aiKbVo.getEmbeddingModelId()) ||
            ObjectUtils.isValidId(aiKbVo.getChatModelId()) ||
            ObjectUtils.isValidId(aiKbVo.getRerankModelId())) {
            return;
        }

        List<AiRelationEntity> relationList = this.aiRelationService.getRelation(aiKbVo.getId());
        if (CollectionUtils.isEmpty(relationList)) {
            return;
        }

        for (AiRelationEntity relation : relationList) {
            if (AiRelationBizTypeEnum.KB_CURRENT_EMBEDDING_MODEL.getValue().equals(relation.getBizType())) {
                AiModelVo aiModelVo = this.aiModelService.getAiModel(AiModelGetRequest.builder().id(relation.getBizId()).build());
                if (aiModelVo == null) {
                    throw new ServiceException(AI_INVALID_KB_MODEL);
                }
                aiKbVo.setEmbeddingModelId(relation.getBizId());
                aiKbVo.setEmbeddingModel(aiModelVo);
            }

            if (AiRelationBizTypeEnum.KB_CURRENT_CHAT_MODEL.getValue().equals(relation.getBizType())) {
                AiModelVo aiModelVo = this.aiModelService.getAiModel(AiModelGetRequest.builder().id(relation.getBizId()).build());
                if (aiModelVo == null) {
                    throw new ServiceException(AI_INVALID_KB_MODEL);
                }
                aiKbVo.setChatModelId(relation.getBizId());
                aiKbVo.setChatModel(aiModelVo);
            }

            if (AiRelationBizTypeEnum.KB_CURRENT_RERANK_MODEL.getValue().equals(relation.getBizType())) {
                AiModelVo aiModelVo = this.aiModelService.getAiModel(AiModelGetRequest.builder().id(relation.getBizId()).build());
                if (aiModelVo == null) {
                    throw new ServiceException(AI_INVALID_KB_MODEL);
                }
                aiKbVo.setRerankModelId(relation.getBizId());
                aiKbVo.setRerankModel(aiModelVo);
            }
        }
    }

}
