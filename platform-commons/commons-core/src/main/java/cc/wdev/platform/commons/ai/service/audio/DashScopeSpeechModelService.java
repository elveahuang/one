package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelSpeechConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleSpeechRequest;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildSpeechModelConfig;

/**
 * @author elvea
 */
public class DashScopeSpeechModelService extends AbstractSpeechModelService {

    public DashScopeSpeechModelService(ModelCommonsConfig commonsConfig, ModelSpeechConfig modelConfig) {
        super(buildSpeechModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see SpeechModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.ALIYUN_DASHSCOPE_SDK;
    }

    /**
     * @see SpeechModelService#call(SimpleSpeechRequest)
     */
    @Override
    public String call(SimpleSpeechRequest request) throws NoApiKeyException, UploadFileException {
        return "";
    }

}
