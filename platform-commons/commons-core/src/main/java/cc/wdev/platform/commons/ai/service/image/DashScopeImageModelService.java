package cc.wdev.platform.commons.ai.service.image;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelImageConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleImageRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleImageResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.ModelService;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.HashMap;
import java.util.Map;

import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_IMAGE_SIZE;
import static cc.wdev.platform.commons.ai.utils.AiUtils.buildImageModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
public class DashScopeImageModelService extends AbstractImageModelService {

    public DashScopeImageModelService(ModelCommonsConfig commonsConfig, ModelImageConfig modelConfig) {
        super(buildImageModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see ModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.ALIYUN_DASHSCOPE_SDK;
    }

    /**
     * @see ImageModelService#call(SimpleImageRequest)
     */
    @Override
    public SimpleImageResponse<ImageSynthesisResult> call(SimpleImageRequest request) {
        ImageSynthesisResult result;
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("watermark", request.isWatermarkEnabled());

            ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(this.config.getApiKey())
                .model(this.config.getName())
                .prompt(request.getPrompt())
                .n(request.getN() <= 0 ? 1 : request.getN())
                .size(nvl(request.getSize(), DEFAULT_IMAGE_SIZE))
                .parameters(parameters)
                .build();

            ImageSynthesis synthesis = new ImageSynthesis();
            result = synthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        SimpleImageResponse<ImageSynthesisResult> response = new SimpleImageResponse<>();
        response.setResult(result);
        return response;
    }

    /**
     * @see ImageModelService#asyncCall(SimpleImageRequest)
     */
    @Override
    public SimpleImageResponse<?> asyncCall(SimpleImageRequest request) {
        return new SimpleImageResponse<>();
    }

}
