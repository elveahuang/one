package cc.wdev.platform.commons.ai.factory;

import cc.wdev.platform.commons.ai.enums.AiModelProvider;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.enums.BaseEnum;
import org.springframework.ai.model.Model;

/**
 * @author elvea
 */
public interface ModelFactory<T extends Model<?, ?>> {

    /**
     * 获取服务提供商
     *
     * @return 服务提供商
     *
     */
    AiServiceProvider getServiceProvider();

    /**
     * 获取模型类型
     *
     * @return 模型类型
     */
    AiModelType getModelType();

    /**
     * 检查是否支持指定的模型配置
     *
     * @param config 模型配置
     * @return 是否支持
     */
    default boolean supports(ModelConfig config) {
        AiServiceProvider serviceProvider = getServiceProvider();
        AiModelProvider modelProvider = BaseEnum.getEnumByValue(config.getModelProvider(), AiModelProvider.class);
        return modelProvider != null && serviceProvider != null
            && serviceProvider.getValue().equalsIgnoreCase(config.getServiceProvider())
            && modelProvider.supportsModel(config.getName());
    }

    /**
     * 获取默认模型配置
     *
     * @return 模型配置
     */
    ModelConfig getModelConfig();

    /**
     * 获取默认模型
     *
     * @return 模型
     */
    default T getModel() {
        return this.getModel(getModelConfig());
    }

    /**
     * 获取指定模型
     *
     * @param config 模型配置
     * @return 模型
     */
    T getModel(ModelConfig config);

}
