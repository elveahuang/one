package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.ai.domain.entity.AiKbTaskEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbTaskSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbTaskVo;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库向量化任务服务
 *
 * @author elvea
 */
public interface AiKbTaskService extends EntityService<AiKbTaskEntity, Long> {

    /**
     * 查询向量任务
     */
    AiKbTaskVo getKbTask(GetRequest request);

    /**
     * 分页查询向量任务
     */
    Page<AiKbTaskEntity> findByPage(AiKbTaskSearchRequest request);

    /**
     * 查询到期待重试任务
     */
    List<AiKbTaskEntity> findDueRetryTasks(LocalDateTime now);

    /**
     * 查询处理超时任务
     */
    List<AiKbTaskEntity> findProcessingTimeoutTasks(LocalDateTime now, int maxProcessingMinutes);

    /**
     * 标记处理中
     */
    void markProcessing(Long id);

    /**
     * 更新进度
     */
    void markProgress(Long id, int total, int processed);

    /**
     * 标记完成
     */
    void markCompleted(Long id);

    /**
     * 标记失败
     */
    void markFailed(Long id, String errorMsg);

    /**
     * 重置为待重试状态
     */
    void markPendingForRetry(Long id, LocalDateTime nextRetryAt);

    /**
     * 统计知识库失败任务数
     */
    long countFailedByKbId(Long kbId);

}
