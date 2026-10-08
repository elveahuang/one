package cc.wdev.platform.commons.ai.service.embedding;

import cc.wdev.platform.commons.ai.domain.request.SimpleEmbeddingRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleEmbeddingResponse;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.service.ModelService;

/**
 * @author elvea
 */
public interface EmbeddingModelService extends ModelService {

    /**
     * @see ModelService#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.EMBEDDING;
    }

    /**
     *
     */
    SimpleEmbeddingResponse<?> call(SimpleEmbeddingRequest request);

}
