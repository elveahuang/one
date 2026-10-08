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

    /**
     * 删除会话事件
     */
    void deleteBySessionId(String sessionId);

    /**
     * 查询会话的未归档事件
     */
    List<AiSessionEventEntity> findBySessionId(String sessionId);

    /**
     * 将指定事件标记为归档
     */
    void archiveByIds(String sessionId, List<String> eventIds);

    AiSessionEventEntity findBySessionEventId(String sessionEventId);

    List<AiSessionEventEntity> findFromSequence(String sessionId, Long fromSequence);

    boolean deleteFromSequence(String sessionId, Long fromSequence);

    List<AiSessionEventEntity> findTurnHead(String sessionId, String firstEventId, EventFilter filter);

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
