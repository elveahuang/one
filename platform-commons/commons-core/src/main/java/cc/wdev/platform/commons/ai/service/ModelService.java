package cc.wdev.platform.commons.ai.service;

import cc.wdev.platform.commons.ai.enums.AiModelProvider;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.enums.BaseEnum;

/**
 * @author elvea
 */
public interface ModelService {

    /**
     * 获取服务提供商
     */
    AiServiceProvider getServiceProvider();

    /**
     * 获取模型类型
     */
    AiModelType getModelType();

    /**
     * 获取默认模型配置
     *
     * @return 模型配置
     */
    ModelConfig getModelConfig();

    /**
     * 检查是否支持指定的模型配置
     */
    default boolean supports(ModelConfig config) {
        AiServiceProvider serviceProvider = getServiceProvider();
        AiModelProvider modelProvider = BaseEnum.getEnumByValue(config.getModelProvider(), AiModelProvider.class);
        return modelProvider != null && serviceProvider != null
            && serviceProvider.getValue().equalsIgnoreCase(config.getServiceProvider())
            && modelProvider.supportsModel(config.getName());
    }

}
