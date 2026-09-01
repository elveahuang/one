package cc.wdev.platform.commons.ai.factory.embedding;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelEmbeddingConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import com.openai.client.OpenAIClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.ai.openai.setup.OpenAiSetup;
import org.springframework.beans.factory.ObjectProvider;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildEmbeddingModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class OpenAiEmbeddingModelFactory extends AbstractEmbeddingModelFactory {

    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<MeterRegistry> meterRegistry;
    private final ObjectProvider<EmbeddingModelObservationConvention> observationConvention;
    private final ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers;

    public OpenAiEmbeddingModelFactory(
        ModelCommonsConfig commonsConfig,
        ModelEmbeddingConfig modelConfig,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<EmbeddingModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        super(buildEmbeddingModelConfig(commonsConfig, modelConfig));

        this.observationRegistry = observationRegistry;
        this.meterRegistry = meterRegistry;
        this.observationConvention = observationConvention;
        this.httpClientBuilderCustomizers = httpClientBuilderCustomizers;
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
    public OpenAiEmbeddingModel getModel(ModelConfig config) {
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
            .model(nvl(config.getName(), OpenAiEmbeddingOptions.DEFAULT_EMBEDDING_MODEL))
            .build();

        log.info("Get OpenAiEmbeddingModel with model {}.", options.getModel());
        log.info("Get OpenAiEmbeddingModel with apiKey {}.", SensitiveUtils.apiKey(options.getApiKey()));
        log.info("Get OpenAiEmbeddingModel with baseUrl {}.", options.getBaseUrl());

        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .openAiClient(this.openAiClient(config))
            .options(options)
            .observationRegistry(this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .httpClientBuilderCustomizers(this.httpClientBuilderCustomizers.orderedStream().toList())
            .metadataMode(MetadataMode.EMBED)
            .build();

        this.observationConvention.ifAvailable(embeddingModel::setObservationConvention);

        return embeddingModel;
    }

    private OpenAIClient openAiClient(ModelConfig config) {
        log.info("Get OpenAIClient for OpenAiEmbeddingModel");
        return OpenAiSetup.setupSyncClient(config.getBaseUrl(), config.getApiKey(), null,
            null, null,
            null, false, false,
            config.getName(), config.getTimeout(), config.getMaxRetries(), config.getProxy(),
            config.getHeaders(),
            this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            this.meterRegistry.getIfAvailable(),
            this.httpClientBuilderCustomizers.orderedStream().toList());
    }

}
