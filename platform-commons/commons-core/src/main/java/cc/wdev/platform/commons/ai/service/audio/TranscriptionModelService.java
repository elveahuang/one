package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.domain.request.SimpleTranscriptionRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleTranscriptionResponse;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.service.ModelService;
import cc.wdev.platform.commons.exception.ServiceException;

/**
 * @author elvea
 */
public interface TranscriptionModelService extends ModelService {

    @Override
    default AiModelType getModelType() {
        return AiModelType.AUDIO_TRANSCRIPTION;
    }

    SimpleTranscriptionResponse<?, ?> call(SimpleTranscriptionRequest request) throws ServiceException;

}
