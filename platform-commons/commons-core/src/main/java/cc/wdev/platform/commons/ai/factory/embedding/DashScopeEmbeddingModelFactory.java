package cc.wdev.platform.commons.ai.factory.embedding;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelEmbeddingConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import io.github.agentic.spring.ai.dashscope.sdk.embedding.DashScopeSdkEmbeddingModel;
import io.github.agentic.spring.ai.dashscope.sdk.embedding.DashScopeSdkEmbeddingOptions;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.retry.RetryTemplate;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildEmbeddingModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DashScopeEmbeddingModelFactory extends AbstractEmbeddingModelFactory {

    private final ObjectProvider<RetryTemplate> retryTemplate;
    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<EmbeddingModelObservationConvention> observationConvention;

    public DashScopeEmbeddingModelFactory(
        ModelCommonsConfig commonsConfig,
        ModelEmbeddingConfig modelConfig,
        ObjectProvider<RetryTemplate> retryTemplate,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<EmbeddingModelObservationConvention> observationConvention
    ) {
        super(buildEmbeddingModelConfig(commonsConfig, modelConfig));

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
    public DashScopeSdkEmbeddingModel getModel(ModelConfig config) {
        DashScopeSdkEmbeddingOptions options = DashScopeSdkEmbeddingOptions.builder()
            .model(nvl(config.getName(), DashScopeSdkEmbeddingModel.DEFAULT_MODEL_NAME))
            .build();

        DashScopeSdkEmbeddingModel embeddingModel = DashScopeSdkEmbeddingModel.builder()
            .apiKey(config.getApiKey())
            .workspaceId(config.getWorkspaceId())
            .connectionHeaders(config.getHeaders())
            .defaultOptions(options)
            .retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        observationConvention.ifAvailable(embeddingModel::setObservationConvention);
        return embeddingModel;
    }

}
