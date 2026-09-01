package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelSpeechConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import com.openai.client.OpenAIClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.ai.openai.setup.OpenAiSetup;
import org.springframework.beans.factory.ObjectProvider;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildSpeechModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class OpenAiSpeechModelFactory extends AbstractSpeechModelFactory {

    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<MeterRegistry> meterRegistry;
    private final ObjectProvider<ChatModelObservationConvention> observationConvention;
    private final ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers;

    public OpenAiSpeechModelFactory(ModelCommonsConfig commonsConfig,
                                    ModelSpeechConfig modelConfig,
                                    ObjectProvider<ObservationRegistry> observationRegistry,
                                    ObjectProvider<MeterRegistry> meterRegistry,
                                    ObjectProvider<ChatModelObservationConvention> observationConvention,
                                    ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        super(buildSpeechModelConfig(commonsConfig, modelConfig));

        this.observationRegistry = observationRegistry;
        this.meterRegistry = meterRegistry;
        this.observationConvention = observationConvention;
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
    public OpenAiAudioSpeechModel getModel(ModelConfig config) {
        log.info("Get OpenAI TextToSpeechModel.");
        OpenAiAudioSpeechOptions.Builder optionsBuilder = OpenAiAudioSpeechOptions.builder();
        optionsBuilder.model(nvl(config.getName(), OpenAiAudioSpeechOptions.DEFAULT_SPEECH_MODEL));
        optionsBuilder.speed(OpenAiAudioSpeechOptions.DEFAULT_SPEED);
        optionsBuilder.voice(OpenAiAudioSpeechOptions.DEFAULT_VOICE);
        optionsBuilder.responseFormat(OpenAiAudioSpeechOptions.DEFAULT_RESPONSE_FORMAT);

        OpenAIClient client = openAiClient(config);
        return OpenAiAudioSpeechModel.builder()
            .openAiClient(client)
            .options(optionsBuilder.build())
            .httpClientBuilderCustomizers(httpClientBuilderCustomizers.orderedStream().toList())
            .build();
    }

    private OpenAIClient openAiClient(ModelConfig config) {
        log.info("Get OpenAIClient for OpenAiAudioSpeechModel");
        return OpenAiSetup.setupSyncClient(config.getBaseUrl(), config.getApiKey(), null,
            null, null,
            null, false, false,
            config.getName(), config.getTimeout(), config.getMaxRetries(), config.getProxy(),
            config.getHeaders(),
            observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            meterRegistry.getIfAvailable(),
            httpClientBuilderCustomizers.orderedStream().toList());
    }

}
