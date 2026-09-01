package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.ai.enums.AiVectorizationStatus;
import cc.wdev.platform.commons.concurrent.AsyncExecutor;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiKbItemConverter;
import cc.wdev.platform.system.ai.domain.entity.AiKbChunkEntity;
import cc.wdev.platform.system.ai.domain.entity.AiKbItemEntity;
import cc.wdev.platform.system.ai.domain.entity.AiKbTaskEntity;
import cc.wdev.platform.system.ai.domain.vo.AiKbItemVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbTaskVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import cc.wdev.platform.system.ai.service.*;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder.Op;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cc.wdev.platform.system.ai.constants.SystemAiConstants.VECTOR_TASK_TYPE_KB;
import static cc.wdev.platform.system.ai.constants.SystemAiConstants.VECTOR_TASK_TYPE_KB_ITEM;

/**
 * 知识库向量化与检索核心实现
 *
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiVectorServiceImpl implements AiVectorService {

    private final AiHelper aiHelper;

    private final AiKbService aiKbService;

    private final AiKbItemService aiKbItemService;

    private final AiKbChunkService aiKbChunkService;

    private final AiKbTaskService aiKbTaskService;

    /**
     * @see AiVectorService#submitKbItemTask(Long, Long)
     */
    @Override
    public void submitKbItemTask(Long kbId, Long kbItemId) {
        // 创建任务
        AiKbTaskEntity task = this.createTask(kbId, kbItemId, VECTOR_TASK_TYPE_KB_ITEM);
        // 异步执行任务
        AsyncExecutor.execute(() -> this.executeKbItemTask(task.getId()));
    }

    /**
     * @see AiVectorService#executeKbItemTask(Long)
     */
    @Override
    public void executeKbItemTask(final Long taskId) {
        // 检测任务是否存在
        AiKbTaskVo task = this.aiKbTaskService.getKbTask(GetRequest.builder().id(taskId).build());

        this.aiKbTaskService.markProcessing(taskId);
        try {
            // 检测知识库
            AiKbVo kb = this.aiKbService.getKb(GetRequest.builder().id(task.getKbId()).build());
            // 检测知识分片
            AiKbItemVo kbItem = this.aiKbItemService.getKbItem(GetRequest.builder().id(task.getKbItemId()).build());

            // 处理分片并向量化
            int count = this.vectorizeItem(kb, kbItem);

            // 标记任务状态
            this.aiKbTaskService.markProgress(taskId, count, count);
            this.aiKbTaskService.markCompleted(taskId);
            log.info("Vectorization item task completed, taskId={}, itemId={}, chunks={}", taskId, kbItem.getId(), count);
        } catch (Exception e) {
            log.error("Vectorization item task failed, taskId={}, itemId={}", taskId, task.getKbItemId(), e);
            this.handleFailure(taskId, task.getKbItemId(), e);
        }
    }

    /**
     * @see AiVectorService#submitKbTask(Long)
     */
    @Override
    public void submitKbTask(Long kbId) {
        AiKbTaskEntity task = this.createTask(kbId, 0L, VECTOR_TASK_TYPE_KB);
        AsyncExecutor.execute(() -> this.executeKbTask(task.getId()));
    }

    /**
     * @see AiVectorService#executeKbTask(Long)
     */
    @Override
    public void executeKbTask(Long taskId) {
        // 检测任务是否存在
        AiKbTaskVo task = this.aiKbTaskService.getKbTask(GetRequest.builder().id(taskId).build());

        this.aiKbTaskService.markProcessing(taskId);
        try {
            log.info("KB Vectorization rebuild task [{}] start", taskId);

            // 检测知识库
            AiKbVo kb = this.aiKbService.getKb(GetRequest.builder().id(task.getKbId()).build());

            // 清理向量
            this.deleteByKb(kb);

            // 清理分片
            this.aiKbChunkService.deleteByKbId(kb.getId());

            // 查询已有分片
            List<AiKbItemEntity> items = this.aiKbItemService.findByKbId(kb.getId(), 1000);

            int success = 0;
            for (AiKbItemEntity entity : items) {
                AiKbItemVo item = AiKbItemConverter.INSTANCE.entity2Vo(entity);
                try {
                    log.info("KB Item [{}] Vectorization rebuild task [{}] start", item.getId(), taskId);
                    this.vectorizeItem(kb, item);
                    success++;
                    log.info("KB Item [{}] Vectorization rebuild task [{}] end", item.getId(), taskId);
                } catch (Exception e) {
                    log.error("KB Item [{}] Vectorization rebuild task [{}] failed", item.getId(), taskId);
                    this.aiKbItemService.markFailed(item.getId(), e.getMessage());
                }
            }
            this.aiKbTaskService.markProgress(taskId, items.size(), success);
            this.aiKbTaskService.markCompleted(taskId);
            log.info("KB Vectorization rebuild task [{}] end", taskId);
        } catch (Exception e) {
            log.error("KB Vectorization rebuild task [{}] failed", taskId, e);
            this.handleFailure(taskId, null, e);
        }
    }

    /**
     * @see AiVectorService#retryTask(Long)
     */
    @Override
    public void retryTask(Long taskId) {
        AiKbTaskVo task = this.aiKbTaskService.getKbTask(GetRequest.builder().id(taskId).build());
        if (VECTOR_TASK_TYPE_KB_ITEM.equals(task.getTaskType())) {
            this.executeKbItemTask(taskId);
        } else if (VECTOR_TASK_TYPE_KB.equals(task.getTaskType())) {
            this.executeKbTask(taskId);
        }
    }

    /**
     * @see AiVectorService#vectorizeItem(AiKbVo, AiKbItemVo)
     */
    @Override
    public int vectorizeItem(@NonNull AiKbVo kb, @NonNull AiKbItemVo kbItem) {
        Long kbId = kb.getId();
        Long kbItemId = kbItem.getId();
        log.info("KB [{}] Item [{}] Vectorization start", kbId, kbItemId);

        // 获取知识分片
        List<AiKbChunkEntity> entities = this.aiKbChunkService.findByKbItemId(kbItemId);
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }

        List<Document> documents = Lists.newArrayList();
        for (AiKbChunkEntity entity : entities) {
            Map<String, Object> metadata = GsonUtils.toObjectMap(entity.getMetadata());

            Document document = Document.builder()
                .id(entity.getVectorDocId())
                .text(entity.getContent())
                .metadata(metadata)
                .build();

            documents.add(document);
        }

        if (CollectionUtils.isNotEmpty(entities)) {
            try {
                // 获取知识库附加信息，包含模型等关联的信息，避免下面因为没有模型信息导致无法获取向量存储而报错
                this.aiHelper.getAiKbExtra(kb);
                // 获取知识库向量存储
                VectorStore vectorStore = this.aiHelper.resolveVectorStore(kb);
                // 向量化切片
                this.aiHelper.vectorize(vectorStore, documents);
            } catch (Exception e) {
                log.error("KB [{}] Item [{}] Vectorization failed", kbId, kbItemId, e);
            }
        }

        // 回写向量化结果
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);

            AiKbChunkEntity update = new AiKbChunkEntity();
            update.setId(entities.get(i).getId());
            update.setVectorDocId(document.getId());
            update.setVectorized(BooleanTypeEnum.TRUE.getValue());
            this.aiKbChunkService.updateById(update);
        }

        // 标记向量化完成
        this.markItemCompleted(kbItem);

        return documents.size();
    }

    /**
     * @see AiVectorService#deleteByKb(AiKbVo)
     */
    @Override
    public void deleteByKb(AiKbVo kb) {
        log.debug("KB [{}] collection [{}] RAG delete by kb start", kb.getId(), kb.getCollectionName());
        VectorStore vectorStore = this.aiHelper.resolveVectorStore(kb);
        vectorStore.delete(this.aiHelper.buildScopeFilter(kb));
        log.debug("KB [{}] collection [{}] RAG delete by kb end", kb.getId(), kb.getCollectionName());
    }

    /**
     * @see AiVectorService#deleteByKbItemId(AiKbVo, Long)
     */
    @Override
    public void deleteByKbItemId(@NonNull AiKbVo kb, @NonNull Long kbItemId) {
        log.debug("KB [{}] collection [{}] RAG delete by item [{} ]start", kb.getId(), kb.getCollectionName(), kbItemId);
        VectorStore vectorStore = this.aiHelper.resolveVectorStore(kb);
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Op op = builder.eq(AiHelper.METADATA_TENANT_ID, this.aiHelper.resolveTenant(kb));
        op = builder.and(op, builder.eq(AiHelper.METADATA_KB_ITEM_ID, kbItemId));
        vectorStore.delete(op.build());
        log.debug("KB [{}] collection [{}] RAG delete by item [{}] start", kb.getId(), kb.getCollectionName(), kbItemId);
    }

    /**
     * 失败处理：重试退避（nextRetryAt），超过重试上限标记失败并回写条目状态
     */
    private void handleFailure(Long taskId, Long kbItemId, Exception e) {
        this.aiKbTaskService.markFailed(taskId, e.getMessage());
        this.aiKbItemService.markFailed(kbItemId, e.getMessage());
    }

    // ------------------------------------------------------------------------------
    // 私有辅助方法
    // ------------------------------------------------------------------------------

    /**
     * 创建任务
     */
    private AiKbTaskEntity createTask(Long kbId, Long kbItemId, String taskType) {
        AiKbTaskEntity task = new AiKbTaskEntity();
        task.setTenantId(TenantContext.getTenantId());
        task.setKbId(kbId);
        task.setKbItemId(kbItemId);
        task.setTaskId(StringUtils.uuid());
        task.setTaskType(taskType);
        task.setStatus(AiVectorizationStatus.PENDING.getValue());
        this.aiKbTaskService.save(task);
        return task;
    }

    private void markItemCompleted(AiKbItemVo item) {
        AiKbItemEntity update = new AiKbItemEntity();
        update.setId(item.getId());
        update.setVectorized(BooleanTypeEnum.TRUE.getValue());
        update.setStatus(AiVectorizationStatus.COMPLETED.getValue());
        this.aiKbItemService.updateById(update);
    }

}
