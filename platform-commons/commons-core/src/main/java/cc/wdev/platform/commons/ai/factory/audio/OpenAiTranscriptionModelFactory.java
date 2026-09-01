package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelTranscriptionConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import com.openai.client.OpenAIClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.ai.openai.setup.OpenAiSetup;
import org.springframework.beans.factory.ObjectProvider;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildTranscriptionModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class OpenAiTranscriptionModelFactory extends AbstractTranscriptionModelFactory {

    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<MeterRegistry> meterRegistry;
    private final ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers;

    public OpenAiTranscriptionModelFactory(ModelCommonsConfig commonsConfig,
                                           ModelTranscriptionConfig modelConfig,
                                           ObjectProvider<ObservationRegistry> observationRegistry,
                                           ObjectProvider<MeterRegistry> meterRegistry,
                                           ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        super(buildTranscriptionModelConfig(commonsConfig, modelConfig));

        this.observationRegistry = observationRegistry;
        this.meterRegistry = meterRegistry;
        this.httpClientBuilderCustomizers = httpClientBuilderCustomizers;
    }

    /**
     * @see ModelFactory#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.SPRING_AI_OPENAI;
    }

    /**
     * @see ModelFactory#getModel(ModelConfig)
     */
    @Override
    public OpenAiAudioTranscriptionModel getModel(@NonNull ModelConfig config) {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
            .model(nvl(config.getName(), OpenAiAudioTranscriptionOptions.DEFAULT_TRANSCRIPTION_MODEL))
            .responseFormat(OpenAiAudioTranscriptionOptions.DEFAULT_RESPONSE_FORMAT)
            .build();

        log.info("Get OpenAiAudioTranscriptionModel with model {}.", options.getModel());
        log.info("Get OpenAiAudioTranscriptionModel with apiKey {}", SensitiveUtils.apiKey(options.getApiKey()));
        log.info("Get OpenAiAudioTranscriptionModel with baseUrl {}", options.getBaseUrl());

        return OpenAiAudioTranscriptionModel.builder()
            .openAiClient(this.openAiClient(config))
            .options(options)
            .build();
    }

    private OpenAIClient openAiClient(ModelConfig config) {
        log.info("Get OpenAIClient for OpenAiAudioTranscriptionModel");
        return OpenAiSetup.setupSyncClient(config.getBaseUrl(), config.getApiKey(),
            null, null,
            null, null,
            false, false, config.getName(), config.getTimeout(), config.getMaxRetries(),
            config.getProxy(),
            config.getHeaders(),
            this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            this.meterRegistry.getIfAvailable(),
            this.httpClientBuilderCustomizers.orderedStream().toList());
    }

}
