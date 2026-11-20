package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface AiSessionApi {

    /**
     * 获取会话历史记录
     */
    Page<AiSessionEventVo> findHistory(AiSessionEventRequest request);

    /**
     * 获取当前会话记录
     */
    Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request);

    /**
     * 确保会话存在并在当日无消息时写入开场白事件
     */
    String resolveGreetingEvent(AiSessionEventSaveRequest request);

    /**
     * 更新事件 metadata（如开场白/交互卡片的 uiState）
     */
    boolean updateEventMetadata(Long tenantId, Long userId, String sessionId, String eventId, String metadataJson);

}
