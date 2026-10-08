package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
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

}
