package cc.wdev.platform.commons.ai.factory.image;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelImageConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.embedding.EmbeddingModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import com.openai.client.OpenAIClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.observation.ImageModelObservationConvention;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.ai.openai.setup.OpenAiSetup;
import org.springframework.beans.factory.ObjectProvider;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildImageModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class OpenAiImageModelFactory extends AbstractImageModelFactory {

    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<MeterRegistry> meterRegistry;
    private final ObjectProvider<ImageModelObservationConvention> observationConvention;
    private final ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers;

    public OpenAiImageModelFactory(
        ModelCommonsConfig commonsConfig,
        ModelImageConfig modelConfig,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<ImageModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        super(buildImageModelConfig(commonsConfig, modelConfig));

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
    public OpenAiImageModel getModel(ModelConfig config) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .model(nvl(config.getName(), OpenAiImageOptions.DEFAULT_IMAGE_MODEL))
            .build();

        log.info("Get OpenAiImageModel with model {}.", options.getModel());
        log.info("Get OpenAiImageModel with apiKey {}", SensitiveUtils.apiKey(options.getApiKey()));
        log.info("Get OpenAiImageModel with baseUrl {}", options.getBaseUrl());

        OpenAiImageModel imageModel = OpenAiImageModel.builder()
            .openAiClient(this.openAiClient(config))
            .options(options)
            .observationRegistry(this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .httpClientBuilderCustomizers(this.httpClientBuilderCustomizers.orderedStream().toList())
            .build();

        this.observationConvention.ifAvailable(imageModel::setObservationConvention);

        return imageModel;
    }

    private OpenAIClient openAiClient(ModelConfig config) {
        log.info("Get OpenAIClient for OpenAiImageModel");
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
