package cc.wdev.platform.commons.ai.service.embedding;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelEmbeddingConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleEmbeddingRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleEmbeddingResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.ModelService;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildEmbeddingModelConfig;

/**
 * @author elvea
 */
public class DashScopeEmbeddingModelService extends AbstractEmbeddingModelService {

    public DashScopeEmbeddingModelService(ModelCommonsConfig commonsConfig, ModelEmbeddingConfig modelConfig) {
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
        SimpleEmbeddingResponse<TextEmbeddingResult> response = new SimpleEmbeddingResponse<>();
        try {
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                .apiKey(this.config.getApiKey())
                .model(this.config.getName())
                .texts(request.getTexts())
                .build();

            TextEmbedding textEmbedding = new TextEmbedding();
            TextEmbeddingResult result = textEmbedding.call(param);

            response.setRequestId(result.getRequestId());
            response.setResult(result);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

}
