package cc.wdev.platform.commons.ai.service.image;

import cc.wdev.platform.commons.ai.domain.request.SimpleImageRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleImageResponse;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.service.ModelService;

/**
 * @author elvea
 */
public interface ImageModelService extends ModelService {

    /**
     * 获取模型类型
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.IMAGE;
    }

    /**
     * 同步调用生成图片
     */
    SimpleImageResponse<?> call(SimpleImageRequest request);

    /**
     * 异步调用生成图片
     */
    SimpleImageResponse<?> asyncCall(SimpleImageRequest request);

}
