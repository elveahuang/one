package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelTranscriptionConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleTranscriptionRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleTranscriptionResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.exception.ServiceException;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildTranscriptionModelConfig;

/**
 * @author elvea
 */
public class HunYuanTranscriptionModelService extends AbstractTranscriptionModelService {

    public HunYuanTranscriptionModelService(ModelCommonsConfig commonsConfig, ModelTranscriptionConfig modelConfig) {
        super(buildTranscriptionModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see TranscriptionModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.TENCENT_HUNYUAN_SDK;
    }

    /**
     * @see TranscriptionModelService#call(SimpleTranscriptionRequest)
     */
    @Override
    public SimpleTranscriptionResponse<?, ?> call(SimpleTranscriptionRequest request) throws ServiceException {
        return null;
    }

}
