package cc.wdev.platform.commons.ai.service.chat;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleChatResponse;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.service.ModelService;

/**
 * @author elvea
 */
public interface ChatModelService extends ModelService {

    /**
     * @see ModelService#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.TEXT;
    }

    /**
     * 同步调用生成图片
     */
    SimpleChatResponse<?> call(SimpleChatRequest request);

}
