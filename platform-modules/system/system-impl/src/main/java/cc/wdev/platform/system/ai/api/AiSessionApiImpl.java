package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class AiSessionApiImpl implements AiSessionApi {

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    /**
     * @see AiSessionApi#findHistory
     */
    @Override
    public Page<AiSessionEventVo> findHistory(AiSessionEventRequest request) {
        return aiSessionEventService.findHistory(request);
    }

    /**
     * @see AiSessionApi#findCurrent
     */
    @Override
    public Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request) {
        return aiSessionEventService.findCurrent(request);
    }

}
