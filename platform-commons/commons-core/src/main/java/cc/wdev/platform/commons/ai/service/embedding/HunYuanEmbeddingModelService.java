package cc.wdev.platform.commons.ai.service.embedding;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelEmbeddingConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleEmbeddingRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleEmbeddingResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.ModelService;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildEmbeddingModelConfig;

/**
 * @author elvea
 */
public class HunYuanEmbeddingModelService extends AbstractEmbeddingModelService {

    public HunYuanEmbeddingModelService(ModelCommonsConfig commonsConfig, ModelEmbeddingConfig modelConfig) {
        super(buildEmbeddingModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see ModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.ALIYUN_DASHSCOPE_SDK;
    }

    /**
     * @see EmbeddingModelService#call(SimpleEmbeddingRequest)
     */
    @Override
    public SimpleEmbeddingResponse<TextEmbeddingResult> call(SimpleEmbeddingRequest request) {
        return new SimpleEmbeddingResponse<>();
    }

}
