package cc.wdev.platform.commons.ai.service.chat;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleChatResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.image.ImageModelService;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildChatModelConfig;

/**
 * @author elvea
 */
public class DashScopeChatModelService extends AbstractChatModelService {

    public DashScopeChatModelService(ModelCommonsConfig commonsConfig, ModelChatConfig modelConfig) {
        super(buildChatModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see ImageModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.ALIYUN_DASHSCOPE_SDK;
    }

    /**
     * @see ChatModelService#call(SimpleChatRequest)
     */
    @Override
    public SimpleChatResponse<?> call(SimpleChatRequest request) {
        return SimpleChatResponse.builder().build();
    }

}
