package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.*;
import cc.wdev.platform.system.ai.domain.vo.*;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 知识库服务
 *
 * @author elvea
 */
public interface AiKbApi {

    /**
     * 初始化默认知识库
     */
    void initialize();

    // ------------------------------------------------------------------------------
    // 知识库
    // ------------------------------------------------------------------------------

    /**
     * 获取知识库详情
     */
    AiKbVo getKb(GetRequest request);

    /**
     * 保存知识库
     */
    void saveKb(AiKbSaveRequest request);

    /**
     * 分页查询知识库
     */
    Page<AiKbVo> findKbByPage(AiKbSearchRequest request);

    /**
     * 删除知识库（同时删除向量数据）
     */
    void deleteKb(DeleteRequest request);

    /**
     * 重建知识库向量
     */
    void rebuildKb(GetRequest request);

    // ------------------------------------------------------------------------------
    // 知识库 - 知识条目
    // ------------------------------------------------------------------------------

    /**
     * 查询知识条目详情
     */
    AiKbItemVo getKbItem(GetRequest request);

    /**
     * 获取知识条目
     */
    AiKbItemVo resolveKbItem(AiKbItemResolveRequest request);

    /**
     * 分页查询知识条目
     */
    Page<AiKbItemVo> findItemByPage(AiKbItemSearchRequest request);


    /**
     * 创建文档知识条目
     */
    Long createDocumentItem(AiKbItemSaveRequest request, Resource resource) throws IOException;

    /**
     * 创建文档知识条目
     */
    Long createDocumentItem(AiKbItemSaveRequest request, MultipartFile file);

    /**
     * 创建文档知识条目
     */
    Long createDocumentItem(AiKbItemSaveRequest request, File file);

    /**
     * 创建知识条目
     * 1. 文本知识条目
     * 2. 问答知识条目
     */
    Long createItem(AiKbItemSaveRequest request);

    /**
     * 更新知识条目（重解析并增量重建分片与向量）
     */
    Long updateItem(AiKbItemSaveRequest request);

    /**
     * 删除知识条目（同步清理分片与向量）
     */
    void deleteItem(DeleteRequest request);

    // ------------------------------------------------------------------------------
    // 知识库 - 知识分片
    // ------------------------------------------------------------------------------

    /**
     * 查询条目分片
     */
    List<AiKbChunkVo> findChunks(Long kbItemId);

    /**
     * 创建文档知识条目分片
     */
    List<Long> createChunks(AiKbVo kb, AiKbItemVo kbItem);

    // ------------------------------------------------------------------------------
    // 知识库 - 向量任务
    // ------------------------------------------------------------------------------

    /**
     * 分页查询向量化任务
     */
    Page<AiKbTaskVo> findVectorTasks(AiKbTaskSearchRequest request);

    /**
     * 查询向量化任务详情
     */
    AiKbTaskVo getVectorTask(Long id);

    /**
     * 重试失败/待重试的向量化任务
     */
    void retryTask(Long id);

    // ------------------------------------------------------------------------------
    // 知识检索
    // ------------------------------------------------------------------------------

    /**
     * 查询知识库统计
     */
    AiKbStatsVo getKbStats(Long kbId);

    /**
     * 知识库检索评估（问题集逐条召回）
     */
    List<AiKbEvalResultVo> evalKb(AiKbEvalRequest request);

    /**
     * 知识库语义检索
     */
    List<AiKbSearchResultVo> searchKb(Long id, String query, Integer topK, Double similarityThreshold);

    // ------------------------------------------------------------------------------
    // 定时任务
    // ------------------------------------------------------------------------------

    /**
     * 向量化任务补偿调度：
     * 1. 重试到期待重试任务（PENDING + nextRetryAt <= now）；
     * 2. 回收处理超时任务（PROCESSING 超过 maxProcessingMinutes）。
     */
    void execute() throws Exception;

}
