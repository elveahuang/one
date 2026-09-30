package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.session.EventFilter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiSessionEventService extends CachingEntityService<AiSessionEventEntity, Long> {

    void deleteBySessionId(String sessionId);

    /**
     * 根据会话ID查询事件
     */
    List<AiSessionEventEntity> findBySessionId(String sessionId);

    /**
     * 查询会话的 active(未归档)事件
     */
    List<AiSessionEventEntity> findActiveBySessionId(String sessionId);

    /**
     * 将指定事件标记为归档（压缩后保留全量 Recall Storage）
     */
    void archiveByIds(List<Long> ids);

    /**
     * 替换 active 窗口：删除该会话全部 active 事件并以 retainedEvents 重建。用于 compaction。
     */
    void replaceActiveWindow(String sessionId, List<AiSessionEventEntity> retainedEvents);

    List<AiSessionEventEntity> findEvents(@NonNull String sessionId, @NonNull EventFilter filter);

    /**
     * 获取会话历史记录
     */
    Page<AiSessionEventVo> findHistory(AiSessionEventRequest request);

    /**
     * 获取当前会话记录
     */
    Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request);

}
