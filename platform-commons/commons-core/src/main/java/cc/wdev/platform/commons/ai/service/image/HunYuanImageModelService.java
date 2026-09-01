package cc.wdev.platform.commons.ai.service.image;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelImageConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleImageRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleImageResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.service.ModelService;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildImageModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
public class HunYuanImageModelService extends AbstractImageModelService {

    public HunYuanImageModelService(ModelCommonsConfig commonsConfig, ModelImageConfig modelConfig) {
        super(buildImageModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see ImageModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.TENCENT_HUNYUAN_SDK;
    }

    /**
     * @see ModelService#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        SimpleModelConfig.SimpleModelConfigBuilder builder = SimpleModelConfig.builder();
        builder.apiKey(nvl(this.config.getApiKey(), System.getenv("HUNYUAN_API_KEY")));
        builder.baseUrl(this.config.getBaseUrl());
        builder.name(this.config.getName());
        return builder.build();
    }

    /**
     * @see ImageModelService#call(SimpleImageRequest)
     */
    @Override
    public SimpleImageResponse<?> call(SimpleImageRequest request) {
        return new SimpleImageResponse<>();
    }

    /**
     * @see ImageModelService#asyncCall(SimpleImageRequest)
     */
    @Override
    public SimpleImageResponse<?> asyncCall(SimpleImageRequest request) {
        return new SimpleImageResponse<>();
    }

}
