package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
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
     * 根据事件ID查询事件
     */
    AiSessionEventEntity findBySessionEventId(String sessionEventId);

    /**
     * 根据事件ID查询事件
     */
    List<AiSessionEventEntity> findBySessionEventIds(List<String> sessionEventIds);

    /**
     * 批量查询多个会话的用户消息事件（用于列表页批量生成会话标题，避免逐会话查询）
     */
    List<AiSessionEventEntity> findUserMessagesBySessionIds(List<String> sessionIds);

    /**
     * 获取会话历史记录
     */
    Page<AiSessionEventVo> findHistory(AiSessionEventRequest request);

    /**
     * 获取当前会话记录
     */
    Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request);

}
