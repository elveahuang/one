package cc.wdev.platform.commons.ai.factory.image;

import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import org.springframework.ai.image.ImageModel;

/**
 * @author elvea
 */
public interface ImageModelFactory extends ModelFactory<ImageModel> {

    /**
     * 获取模型类型
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.IMAGE;
    }

    /**
     * 获取图像模型
     */
    default ImageModel getImageModel() {
        return this.getImageModel(this.getModelConfig());
    }

    /**
     * 获取图像模型
     */
    default ImageModel getImageModel(ModelConfig config) {
        return this.getModel(config);
    }

}
