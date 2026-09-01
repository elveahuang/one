package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.ai.config.SplittingConfig;
import cc.wdev.platform.commons.ai.core.processor.DocumentProcessor;
import cc.wdev.platform.commons.ai.enums.AiSplittingStrategy;
import cc.wdev.platform.commons.ai.enums.AiVectorizationStatus;
import cc.wdev.platform.commons.ai.utils.AiRagUtils;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.ai.domain.converter.AiKbChunkConverter;
import cc.wdev.platform.system.ai.domain.converter.AiKbConverter;
import cc.wdev.platform.system.ai.domain.converter.AiKbItemConverter;
import cc.wdev.platform.system.ai.domain.converter.AiKbTaskConverter;
import cc.wdev.platform.system.ai.domain.entity.*;
import cc.wdev.platform.system.ai.domain.request.*;
import cc.wdev.platform.system.ai.domain.vo.*;
import cc.wdev.platform.system.ai.enums.AiKbItemTypeEnum;
import cc.wdev.platform.system.ai.enums.AiRelationBizTypeEnum;
import cc.wdev.platform.system.ai.enums.BaseAiKbBizTypeEnum;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import cc.wdev.platform.system.ai.service.*;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import cc.wdev.platform.system.storage.enums.AttachmentRelationBizTypeEnum;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okio.Path;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_CHUNK_LIST_LIMIT;
import static cc.wdev.platform.commons.utils.StringUtils.nvl;
import static cc.wdev.platform.system.ai.constants.SystemAiConstants.VECTOR_TASK_TYPE_KB;
import static cc.wdev.platform.system.ai.constants.SystemAiConstants.VECTOR_TASK_TYPE_KB_ITEM;
import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;
import static java.util.Collections.emptyList;

/**
 * 知识库服务实现
 *
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AiKbApiImpl implements AiKbApi {

    private final AiManager aiManager;

    private final AiHelper aiHelper;

    private final AttachmentApi attachmentApi;

    private final AiKbService aiKbService;

    private final AiKbItemService aiKbItemService;

    private final AiKbChunkService aiKbChunkService;

    private final AiModelService aiModelService;

    private final AiRelationService aiRelationService;

    private final AiVectorService aiVectorService;

    private final AiKbTaskService aiKbTaskService;

    private final AiUsageService aiUsageService;

    /**
     * @see AiKbApi#initialize()
     */
    @Override
    public void initialize() {
        // 扫描枚举定义
        List<BaseAiKbBizTypeEnum> bizTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseAiKbBizTypeEnum.class);

        // 待处理配置项实体
        List<AiKbEntity> updateEntityList = com.google.common.collect.Lists.newArrayList();
        List<AiKbEntity> insertEntityList = com.google.common.collect.Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(bizTypeEnumList)) {
            for (BaseAiKbBizTypeEnum bizTypeEnum : bizTypeEnumList) {
                AiKbEntity entity = this.aiKbService.findByCode(bizTypeEnum.getValue());
                if (entity != null) {
                    updateEntityList.add(entity);
                } else {
                    entity = new AiKbEntity();
                    entity.setCode(bizTypeEnum.getValue());
                    entity.setTitle(bizTypeEnum.getName());
                    entity.setCollectionName(bizTypeEnum.getValue());
                    entity.setDescription(bizTypeEnum.getDescription());
                    insertEntityList.add(entity);
                }
            }
            this.aiKbService.insertBatch(insertEntityList);
            this.aiKbService.updateBatchById(updateEntityList);
        }
    }

    // ------------------------------------------------------------------------------
    // 知识库
    // ------------------------------------------------------------------------------

    /**
     * @see AiKbApi#getKb(GetRequest)
     */
    @Override
    public AiKbVo getKb(GetRequest request) {
        AiKbVo vo = this.aiKbService.getKb(request);
        this.aiHelper.getAiKbExtra(vo);
        return vo;
    }

    /**
     * @see AiKbApi#saveKb(AiKbSaveRequest)
     */
    @Override
    public void saveKb(AiKbSaveRequest request) {
        // 检测向量模型和向量索引是否变更，变更时需要删除已有向量数据再重建
        boolean needRebuild = false;
        if (ObjectUtils.isValidId(request.getId())) {
            AiKbVo vo = this.getKb(GetRequest.builder().id(request.getId()).build());

            boolean collectionChanged = StringUtils.isNotEmpty(vo.getCollectionName())
                && vo.getCollectionName().equalsIgnoreCase(nvl(request.getCollectionName()).trim());
            boolean EmbeddingModelChanged = vo.getEmbeddingModelId() != null
                && !vo.getEmbeddingModelId().equals(request.getEmbeddingModelId());
            if ((collectionChanged || EmbeddingModelChanged)
                && this.aiKbItemService.countByKbId(request.getId()) > 0) {
                needRebuild = true;
            }

            // 删除旧向量
            if (needRebuild) {
                this.aiVectorService.deleteByKb(vo);
            }
        }

        AiKbEntity entity = aiKbService.saveKb(request);
        // 删除关联
        aiRelationService.deleteRelation(List.of(entity.getId()));
        // 保存关联
        aiRelationService.saveKbRelation(entity.getId(), request.getEmbeddingModelId(), request.getChatModelId(), request.getRerankModelId());

        // 重建向量
        if (needRebuild) {
            log.info("KB config changed, rebuild triggered, kbId={}", entity.getId());
            this.aiVectorService.submitKbTask(entity.getId());
        }
    }

    /**
     * @see AiKbApi#findKbByPage(AiKbSearchRequest)
     */
    @Override
    public Page<AiKbVo> findKbByPage(AiKbSearchRequest request) {
        return this.aiKbService.findByPage(request).map(AiKbConverter.INSTANCE::entity2Vo);
    }

    /**
     * @see AiKbApi#deleteKb(DeleteRequest)
     */
    @Override
    public void deleteKb(DeleteRequest request) {
        List<Long> ids = Arrays.asList(request.getIds());
        for (Long id : ids) {
            AiKbVo kb = this.aiKbService.getKb(GetRequest.builder().id(id).build());
            // 清理向量
            this.aiVectorService.deleteByKb(kb);
            // 清理条目
            this.aiKbItemService.deleteByKbId(id);
            // 清理分片
            this.aiKbChunkService.deleteByKbId(id);
            // 清理附近
            this.attachmentApi.deleteAttachmentRelation(AttachmentRelationRequest.builder()
                .bizIdList(List.of(id))
                .bizType(AttachmentBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
                .relationBizType(AttachmentRelationBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
                .build());
            // 清理关联
            this.aiRelationService.deleteRelation(List.of(id));
        }
        this.aiKbService.softDeleteBatchById(ids);
    }

    /**
     * @see AiKbApi#rebuildKb(GetRequest)
     */
    @Override
    public void rebuildKb(GetRequest request) {
        AiKbVo kb = this.getKb(request);
        // 异步重建：清空向量与分片后重新向量化
        this.aiVectorService.submitKbTask(kb.getId());
    }

    // ------------------------------------------------------------------------------
    // 知识库 - 知识条目
    // ------------------------------------------------------------------------------

    /**
     * @see AiKbApi#getKbItem(GetRequest)
     */
    @Override
    public AiKbItemVo getKbItem(GetRequest request) {
        if (request == null || !ObjectUtils.isValidId(request.getId())) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        AiKbItemEntity entity = this.aiKbItemService.findById(request.getId());
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        return AiKbItemConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiKbApi#resolveKbItem(AiKbItemResolveRequest)
     */
    @Override
    public AiKbItemVo resolveKbItem(AiKbItemResolveRequest request) {
        // 检查知识库是否存在
        this.getKb(GetRequest.builder().id(request.getKbId()).code(request.getKbCode()).build());
        // 检查知识条目是否存在
        return this.aiKbItemService.resolveKbItem(request);
    }

    /**
     * @see AiKbApi#findItemByPage(AiKbItemSearchRequest)
     */
    @Override
    public Page<AiKbItemVo> findItemByPage(AiKbItemSearchRequest request) {
        if (StringUtils.isNotEmpty(request.getKbCode())) {
            AiKbVo kb = this.getKb(GetRequest.builder().code(request.getKbCode()).build());
            request.setKbId(kb.getId());
        }
        return this.aiKbItemService.findByPage(request).map(AiKbItemConverter.INSTANCE::entity2Vo);
    }

    /**
     * @see AiKbApi#createDocumentItem(AiKbItemSaveRequest, Resource)
     */
    @Override
    public Long createDocumentItem(AiKbItemSaveRequest request, Resource resource) {
        File temp = null;
        try (InputStream is = resource.getInputStream()) {
            // 创建本地临时文件
            temp = FileUtils.newTempFile(resource.getFilename());
            Files.copy(is, Path.get(temp).toNioPath(), StandardCopyOption.REPLACE_EXISTING);

            return this.createDocumentItem(request, temp);
        } catch (Exception e) {
            log.error("createDocumentItem failed", e);
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        } finally {
            FileUtils.delete(temp);
        }
    }

    /**
     * @see AiKbApi#createDocumentItem(AiKbItemSaveRequest, MultipartFile)
     */
    @Override
    public Long createDocumentItem(AiKbItemSaveRequest request, MultipartFile file) {
        File temp = null;
        try {
            // 创建本地临时文件
            temp = FileUtils.newTempFile(file.getOriginalFilename());
            file.transferTo(temp);

            return this.createDocumentItem(request, temp);
        } catch (Exception e) {
            log.error("createDocumentItem failed", e);
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        } finally {
            FileUtils.delete(temp);
        }
    }

    /**
     * @see AiKbApi#createDocumentItem(AiKbItemSaveRequest, File)
     */
    @Override
    public Long createDocumentItem(AiKbItemSaveRequest request, File file) {
        // 检查知识库是否存在
        AiKbVo kb = this.getKb(GetRequest.builder().id(request.getKbId()).code(request.getKbCode()).build());

        // 上传文档到文件存储，后续有需要的情况，允许重新解析文档并向量化
        AttachmentRequest attachmentRequest = AttachmentRequest.builder()
            .bizType(AttachmentBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
            .build();
        AttachmentFileVo attachmentFileVo = this.attachmentApi.uploadAttachment(attachmentRequest, file);

        // 解析文档并提取文本，文本持久化到知识条目供重建/增量向量化复用
        List<Document> documents = DocumentProcessor.extract(file);
        String text = AiRagUtils.toText(documents);

        AiKbItemEntity item = new AiKbItemEntity();
        item.setKbId(kb.getId());
        item.setBizType(AiKbItemTypeEnum.DOCUMENT.getValue());
        item.setTitle(StringUtils.nvl(request.getTitle(), file.getName()));
        item.setContent(text);
        item.setContentType(StringUtils.nvl(FileUtils.getContentType(file), AiKbItemTypeEnum.DOCUMENT.getValue()));
        item.setContentHash(EncryptUtils.md5Hex(text));
        item.setContentSize((long) text.length());
        item.setExtra(String.valueOf(attachmentFileVo.getId()));
        item.setChunkStrategy(AiSplittingStrategy.TOKEN.getValue());
        item.setVectorized(BooleanTypeEnum.FALSE.getValue());
        item.setStatus(AiVectorizationStatus.PENDING.getValue());
        this.aiKbItemService.save(item);

        // 保存文件和知识条目的关联
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .attachmentIdList(List.of(attachmentFileVo.getId()))
            .relationBizType(AttachmentRelationBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
            .bizType(AttachmentBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
            .bizId(item.getId())
            .build());

        // 创建分片
        this.createChunks(kb, AiKbItemConverter.INSTANCE.entity2Vo(item));

        // 异步向量化
        this.aiVectorService.submitKbItemTask(kb.getId(), item.getId());

        return item.getId();
    }

    /**
     * @see AiKbApi#createItem(AiKbItemSaveRequest)
     */
    @Override
    public Long createItem(AiKbItemSaveRequest request) {
        // 检测知识条目类型
        AiKbItemTypeEnum bizTypeEnum = BaseEnum.getEnumByValue(request.getType(), AiKbItemTypeEnum.class, AiKbItemTypeEnum.NONE);
        if (AiKbItemTypeEnum.NONE.equals(bizTypeEnum)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }

        // 检查知识库是否存在
        AiKbVo kb = this.getKb(GetRequest.builder().id(request.getKbId()).code(request.getKbCode()).build());

        AiKbItemEntity item = new AiKbItemEntity();
        item.setKbId(kb.getId());
        item.setBizType(request.getBizType());
        item.setBizId(request.getId());
        item.setType(bizTypeEnum.getValue());
        item.setTitle(request.getTitle());
        item.setContentType(bizTypeEnum.getValue());
        item.setChunkStrategy(AiSplittingStrategy.TOKEN.getValue());
        item.setVectorized(BooleanTypeEnum.FALSE.getValue());
        item.setStatus(AiVectorizationStatus.PENDING.getValue());

        if (AiKbItemTypeEnum.QA.equals(bizTypeEnum)) {
            String question = nvl(request.getQuestion());
            String answer = nvl(request.getAnswer());
            String content = nvl(question + "\n\n" + answer);

            item.setQuestion(question);
            item.setAnswer(answer);
            item.setContent(content);
            item.setContentSize((long) content.length());
            item.setContentHash(EncryptUtils.md5Hex(content));
        } else {
            String content = nvl(request.getContent());

            item.setContent(content);
            item.setContentSize((long) content.length());
            item.setContentHash(EncryptUtils.md5Hex(content));
        }

        // 同知识库同内容哈希的活跃条目直接跳过
        if (this.aiKbItemService.existsByContentHash(kb.getId(), item.getContentHash())) {
            log.warn("Duplicate item, kbId [{}], hash [{}]", kb.getId(), item.getContentHash());
            return 0L;
        }

        // 保存知识条目
        this.aiKbItemService.save(item);

        // 创建分片
        this.createChunks(kb, AiKbItemConverter.INSTANCE.entity2Vo(item));

        // 异步向量化
        this.aiVectorService.submitKbItemTask(kb.getId(), item.getId());

        return item.getId();
    }

    /**
     * @see AiKbApi#updateItem(AiKbItemSaveRequest)
     */
    @Override
    public Long updateItem(AiKbItemSaveRequest request) {
        // 检测知识条目类型
        AiKbItemTypeEnum bizTypeEnum = BaseEnum.getEnumByValue(request.getType(), AiKbItemTypeEnum.class, AiKbItemTypeEnum.NONE);
        if (AiKbItemTypeEnum.NONE.equals(bizTypeEnum)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }

        // 检测知识库是否存在
        AiKbVo kb = this.getKb(GetRequest.builder().id(request.getKbId()).build());

        // 检测知识条目是否存在
        AiKbItemEntity item = this.aiKbItemService.resolve(request.getId());
        item.setChunkStrategy(AiSplittingStrategy.TOKEN.getValue());
        item.setVectorized(BooleanTypeEnum.FALSE.getValue());
        item.setStatus(AiVectorizationStatus.PENDING.getValue());

        if (AiKbItemTypeEnum.QA.equals(bizTypeEnum)) {
            String question = nvl(request.getQuestion());
            String answer = nvl(request.getAnswer());
            String content = nvl(question + "\n\n" + answer);

            item.setQuestion(question);
            item.setAnswer(answer);
            item.setContent(content);
            item.setContentHash(EncryptUtils.md5Hex(content));
            item.setContentSize((long) content.length());
        } else {
            String content = nvl(request.getContent());

            item.setTitle(request.getTitle());
            item.setContent(content);
            item.setContentHash(EncryptUtils.md5Hex(content));
            item.setContentSize((long) content.length());
        }

        // 保存知识条目
        this.aiKbItemService.updateById(item);

        // 清理旧分片与向量，重新异步向量化
        this.aiKbChunkService.deleteByKbItemId(item.getId());
        this.aiVectorService.deleteByKbItemId(kb, item.getId());

        // 创建分片
        this.createChunks(kb, AiKbItemConverter.INSTANCE.entity2Vo(item));

        // 异步向量化
        this.aiVectorService.submitKbItemTask(kb.getId(), item.getId());

        return item.getId();
    }

    /**
     * @see AiKbApi#deleteItem(DeleteRequest)
     */
    @Override
    public void deleteItem(@NonNull DeleteRequest request) {
        if (ArrayUtils.isEmpty(request.getIds())) {
            return;
        }

        for (Long id : request.getIds()) {
            // 检测知识条目是否存在
            AiKbItemEntity item = this.aiKbItemService.resolve(id);

            // 检测知识库是否存在
            AiKbVo kb = this.getKb(GetRequest.builder().id(item.getKbId()).build());

            // 删除知识分片
            this.aiKbChunkService.deleteByKbItemId(id);

            // 删除知识条目向量数据
            this.aiVectorService.deleteByKbItemId(kb, id);

            // 清理附件关联与文件，只有文档类型的知识条目才需要，普通文本没有附件
            AiKbItemTypeEnum bizTypeEnum = BaseEnum.getEnumByValue(item.getType(), AiKbItemTypeEnum.class, AiKbItemTypeEnum.NONE);
            if (AiKbItemTypeEnum.DOCUMENT.equals(bizTypeEnum)) {
                this.attachmentApi.deleteAttachmentRelation(AttachmentRelationRequest.builder()
                    .bizIdList(List.of(id))
                    .bizType(AttachmentBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
                    .relationBizType(AttachmentRelationBizTypeEnum.KB_ITEM_DOCUMENT.getValue())
                    .build());
            }

            // 删除知识条目
            this.aiKbItemService.deleteById(id);
        }
    }

    // ------------------------------------------------------------------------------
    // 知识库 - 知识分片
    // ------------------------------------------------------------------------------

    /**
     * @see AiKbApi#findChunks(Long)
     */
    @Override
    public List<AiKbChunkVo> findChunks(Long kbItemId) {
        return this.aiKbChunkService.findByKbItemId(kbItemId, DEFAULT_CHUNK_LIST_LIMIT).stream()
            .map(AiKbChunkConverter.INSTANCE::entity2Vo)
            .toList();
    }

    /**
     *
     */
    @Override
    public List<Long> createChunks(@NonNull AiKbVo kb, @NonNull AiKbItemVo kbItem) {
        Long kbId = kb.getId();
        Long kbItemId = kbItem.getId();

        log.info("KB [{}] Item [{}] create chunks start", kbId, kbItemId);
        if (StringUtils.isEmpty(kbItem.getContent())) {
            log.info("KB [{}] Item [{}] create chunks. empty content", kbId, kbItemId);
            return emptyList();
        }

        // 知识条目元数据
        Map<String, Object> kbItemMetadata = Maps.newHashMap();
        if (StringUtils.isNotEmpty(kbItem.getMetadata())) {
            kbItemMetadata.putAll(GsonUtils.toObjectMap(kbItem.getMetadata()));
        }

        // 开始切片
        SplittingConfig config = this.aiHelper.resolveSplittingConfig(kb);
        List<Document> documents = DocumentProcessor.split(kbItem.getContent(), config, kbItemMetadata);

        // 注入元数据
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);

            Map<String, Object> metadata = this.aiHelper.buildDocumentMetadata(AiKbMetadataRequest.builder()
                .tenantId(kb.getTenantId())
                .kbId(kb.getId())
                .kbItemId(kbItem.getId())
                .kbItemType(kbItem.getTitle())
                .kbItemBizType(kbItem.getBizType())
                .kbItemBizId(kbItem.getBizId())
                .kbChunkIndex(i)
                .build(), kbItemMetadata);
            this.aiHelper.applyDocumentMetadata(List.of(document), metadata);
        }

        List<AiKbChunkEntity> entities = new ArrayList<>();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);

            String contentHash = EncryptUtils.md5Hex(document.getText());
            Map<String, Object> meta = document.getMetadata();

            AiKbChunkEntity entity = AiKbChunkEntity.builder()
                .kbId(kb.getId())
                .kbItemId(kbItem.getId())
                .bizType(kbItem.getBizType())
                .title(kbItem.getTitle())
                .content(document.getText())
                .contentType(StringUtils.nvl(kbItem.getContentType(), kbItem.getBizType()))
                .contentSize(StringUtils.isEmpty(document.getText()) ? 0L : (long) document.getText().length())
                .contentHash(contentHash)
                .chunkStrategy(AiSplittingStrategy.TOKEN.getValue())
                .chunkTotal(documents.size())
                .chunkIndex(NumberUtils.toInt(meta.get(AiHelper.METADATA_KB_CHUNK_INDEX)))
                .startIndex(NumberUtils.toInt(meta.get(AiHelper.METADATA_KB_CHUNK_START_INDEX)))
                .endIndex(NumberUtils.toInt(meta.get(AiHelper.METADATA_KB_CHUNK_END_INDEX)))
                .vectorDocId(document.getId())
                .vectorized(BooleanTypeEnum.FALSE.getValue())
                .metadata(GsonUtils.toJson(CollectionUtils.nvl(meta)))
                .status(StatusTypeEnum.ON.getValue())
                .build();
            entities.add(entity);
        }

        if (CollectionUtils.isNotEmpty(entities)) {
            this.aiKbChunkService.saveBatch(entities);
        }

        // 返回分片ID列表
        return entities.stream().map(AiKbChunkEntity::getId).toList();
    }

    // ------------------------------------------------------------------------------
    // 知识库 - 向量任务
    // ------------------------------------------------------------------------------

    /**
     * @see AiKbApi#findVectorTasks(AiKbTaskSearchRequest)
     */
    @Override
    public Page<AiKbTaskVo> findVectorTasks(AiKbTaskSearchRequest request) {
        return this.aiKbTaskService.findByPage(request).map(AiKbTaskConverter.INSTANCE::entity2Vo);
    }

    /**
     * @see AiKbApi#getVectorTask(Long)
     */
    @Override
    public AiKbTaskVo getVectorTask(Long id) {
        AiKbTaskEntity entity = this.aiKbTaskService.findById(id);
        return AiKbTaskConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiKbApi#retryTask(Long)
     */
    @Override
    public void retryTask(Long id) {
        this.aiVectorService.retryTask(id);
    }

    // ------------------------------------------------------------------------------
    // 知识检索
    // ------------------------------------------------------------------------------

    /**
     * @see AiKbApi#getKbStats(Long)
     */
    @Override
    public AiKbStatsVo getKbStats(Long kbId) {
        return AiKbStatsVo.builder()
            .kbId(kbId)
            .itemCount(this.aiKbItemService.countByKbId(kbId))
            .chunkCount(this.aiKbChunkService.countByKbId(kbId))
            .vectorizedItemCount(this.aiKbItemService.countVectorizedByKbId(kbId))
            .pendingItemCount(this.aiKbItemService.countPendingByKbId(kbId))
            .failedItemCount(this.aiKbItemService.countFailedByKbId(kbId))
            .bizTypeCounts(this.aiKbItemService.groupCountByBizType(kbId))
            .failedTaskCount(this.aiKbTaskService.countFailedByKbId(kbId))
            .build();
    }

    /**
     * @see AiKbApi#evalKb(AiKbEvalRequest)
     */
    @Override
    public List<AiKbEvalResultVo> evalKb(AiKbEvalRequest request) {
        List<AiKbEvalResultVo> result = new ArrayList<>(request.getQueries().size());
        for (String query : request.getQueries()) {
            List<AiKbSearchResultVo> hits = this.searchKb(
                request.getKbId(), query, request.getTopK(), request.getSimilarityThreshold());
            result.add(AiKbEvalResultVo.builder()
                .query(query)
                .hits(hits)
                .hitCount(hits.size())
                .build());
        }
        return result;
    }

    /**
     * @see AiKbApi#searchKb(Long, String, Integer, Double)
     */
    @Override
    public List<AiKbSearchResultVo> searchKb(Long id, String query, Integer topK, Double similarityThreshold) {
        AiKbVo kb = this.getKb(GetRequest.builder().id(id).build());

        RetrievalConfig config = this.aiHelper.resolveRetrievalConfig(kb);
        DocumentRetriever retriever = this.aiHelper.resolveDocumentRetriever(kb);
        List<Document> documents = retriever.retrieve(Query.builder().text(query).build());

        // 用量统计
        this.aiUsageService.recordCall(SecurityUtils.getTid(), SecurityUtils.getUid(),
            "SEARCH", kb.getCode(), kb.getId(), null, 1);
        if (config.isRerankEnabled() && this.hasRerankRelation(kb.getId())) {
            this.aiUsageService.recordCall(SecurityUtils.getTid(), SecurityUtils.getUid(),
                "RERANK", kb.getCode(), kb.getId(), null, 1);
        }
        return this.toSearchResults(kb, documents, StringUtils.isNotEmpty(config.getRetrieverType()) ? config.getRetrieverType() : "VECTOR");
    }

    // ------------------------------------------------------------------------------
    // 定时任务
    // ------------------------------------------------------------------------------

    /**
     * @see AiKbApi#execute()
     */
    @Override
    public void execute() throws Exception {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<AiKbTaskEntity> due = this.aiKbTaskService.findDueRetryTasks(now);
            for (AiKbTaskEntity task : due) {
                this.withTenant(task.getTenantId(), () -> this.reExecute(task));
            }
            List<AiKbTaskEntity> timeout = this.aiKbTaskService.findProcessingTimeoutTasks(now, 20);
            for (AiKbTaskEntity task : timeout) {
                this.withTenant(task.getTenantId(), () -> {
                    log.warn("Vectorization task processing timeout, reset and re-run, taskId={}", task.getId());
                    this.aiKbTaskService.markPendingForRetry(task.getId(), null);
                    this.reExecute(task);
                });
            }
            if (!due.isEmpty() || !timeout.isEmpty()) {
                log.info("Vectorization task compensate done, due={}, timeout={}", due.size(), timeout.size());
            }
        } catch (Exception e) {
            log.warn("Vectorization task compensate failed", e);
        }
    }

    private void reExecute(AiKbTaskEntity task) {
        if (VECTOR_TASK_TYPE_KB_ITEM.equals(task.getTaskType())) {
            this.aiVectorService.executeKbItemTask(task.getId());
        } else if (VECTOR_TASK_TYPE_KB.equals(task.getTaskType())) {
            this.aiVectorService.executeKbTask(task.getId());
        }
    }

    /**
     * 定时任务线程无请求上下文，按任务租户显式设置，保证跨租户补偿正确
     */
    private void withTenant(Long tenantId, Runnable runnable) {
        if (tenantId != null && tenantId > 0) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            runnable.run();
        } finally {
            TenantContext.clear();
        }
    }

    // ------------------------------------------------------------------------------
    // 私有辅助方法
    // ------------------------------------------------------------------------------

    /**
     * 检索结果转 VO（携带溯源信息：条目ID/分片序号/偏移）
     */
    private List<AiKbSearchResultVo> toSearchResults(AiKbVo kb, List<Document> documents, String scoreType) {
        if (CollectionUtils.isEmpty(documents)) {
            return List.of();
        }
        // 按检索命中的条目批量加载分片，用于填充 chunkId
        Set<Long> itemIds = documents.stream()
            .map(doc -> MapUtils.getLong(doc.getMetadata(), "kbItemId"))
            .filter(ObjectUtils::isValidId)
            .collect(Collectors.toSet());
        Map<String, AiKbChunkEntity> chunkIndexMap = new HashMap<>();
        for (Long itemId : itemIds) {
            for (AiKbChunkEntity chunk : this.aiKbChunkService.findByKbItemId(itemId, DEFAULT_CHUNK_LIST_LIMIT)) {
                chunkIndexMap.put(itemId + ":" + chunk.getChunkIndex(), chunk);
            }
        }

        List<AiKbSearchResultVo> result = new ArrayList<>();
        for (Document document : documents) {
            Long kbItemId = MapUtils.getLong(document.getMetadata(), "kbItemId");
            Integer chunkIndex = MapUtils.getInteger(document.getMetadata(), "chunkIndex");
            AiKbChunkEntity chunk = chunkIndexMap.get(kbItemId + ":" + chunkIndex);
            result.add(AiKbSearchResultVo.builder()
                .kbId(kb.getId())
                .kbItemId(kbItemId)
                .chunkId(chunk != null ? chunk.getId() : null)
                .chunkIndex(chunkIndex)
                .title(MapUtils.getString(document.getMetadata(), "title"))
                .source(MapUtils.getString(document.getMetadata(), "source"))
                .contentType(MapUtils.getString(document.getMetadata(), "contentType"))
                .startOffset(NumberUtils.toInt(document.getMetadata().get("start")))
                .endOffset(NumberUtils.toInt(document.getMetadata().get("end")))
                .score(document.getScore())
                .scoreType(scoreType)
                .content(document.getText())
                .build());
        }
        return result;
    }

    /**
     * 知识库是否绑定重排模型
     */
    private boolean hasRerankRelation(Long kbId) {
        List<AiRelationEntity> relations = this.aiRelationService.getRelation(kbId);
        if (CollectionUtils.isNotEmpty(relations)) {
            for (AiRelationEntity relation : relations) {
                if (AiRelationBizTypeEnum.KB_CURRENT_RERANK_MODEL.getValue().equals(relation.getBizType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
