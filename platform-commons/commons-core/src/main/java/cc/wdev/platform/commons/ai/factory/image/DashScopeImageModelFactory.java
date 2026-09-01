package cc.wdev.platform.commons.ai.factory.image;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelImageConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.embedding.EmbeddingModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import io.github.agentic.spring.ai.dashscope.sdk.image.DashScopeSdkImageModel;
import io.github.agentic.spring.ai.dashscope.sdk.image.DashScopeSdkImageOptions;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.observation.ImageModelObservationConvention;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.retry.RetryTemplate;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildImageModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DashScopeImageModelFactory extends AbstractImageModelFactory {

    private final ObjectProvider<RetryTemplate> retryTemplate;
    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<ImageModelObservationConvention> observationConvention;

    public DashScopeImageModelFactory(
        ModelCommonsConfig commonsConfig,
        ModelImageConfig modelConfig,
        ObjectProvider<RetryTemplate> retryTemplate,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<ImageModelObservationConvention> observationConvention
    ) {
        super(buildImageModelConfig(commonsConfig, modelConfig));

        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    /**
     * @see EmbeddingModelFactory#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.SPRING_AI_OPENAI;
    }

    /**
     * @see EmbeddingModelFactory#getModel(ModelConfig)
     */
    @Override
    public DashScopeSdkImageModel getModel(ModelConfig config) {
        DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder()
            .model(nvl(config.getName(), DashScopeSdkImageModel.DEFAULT_MODEL_NAME))
            .build();

        DashScopeSdkImageModel imageModel = DashScopeSdkImageModel.builder()
            .apiKey(config.getApiKey())
            .workspaceId(config.getWorkspaceId())
            .connectionHeaders(config.getHeaders())
            .defaultOptions(options)
            .retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        observationConvention.ifAvailable(imageModel::setObservationConvention);
        return imageModel;
    }

}
