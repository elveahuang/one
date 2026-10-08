package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.domain.request.SimpleSpeechRequest;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.service.ModelService;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

/**
 * @author elvea
 */
public interface SpeechModelService extends ModelService {

    /**
     * @see TranscriptionModelService#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.AUDIO_SPEECH;
    }

    String call(SimpleSpeechRequest request) throws NoApiKeyException, UploadFileException;

}
