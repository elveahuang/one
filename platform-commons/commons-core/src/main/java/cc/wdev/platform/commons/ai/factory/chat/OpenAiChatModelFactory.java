package cc.wdev.platform.commons.ai.factory.chat;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientAsync;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.ai.openai.setup.OpenAiSetup;
import org.springframework.beans.factory.ObjectProvider;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildChatModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class OpenAiChatModelFactory extends AbstractChatModelFactory {

    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<MeterRegistry> meterRegistry;
    private final ObjectProvider<ChatModelObservationConvention> observationConvention;
    private final ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers;

    public OpenAiChatModelFactory(ModelCommonsConfig commonsConfig, ModelChatConfig modelConfig,
                                  ObjectProvider<ObservationRegistry> observationRegistry,
                                  ObjectProvider<MeterRegistry> meterRegistry,
                                  ObjectProvider<ChatModelObservationConvention> observationConvention,
                                  ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers) {
        super(buildChatModelConfig(commonsConfig, modelConfig));

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
     * @see ChatModelFactory#getModel(ModelConfig)
     */
    @Override
    public OpenAiChatModel getModel(ModelConfig config) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
            .model(nvl(config.getName(), OpenAiChatOptions.DEFAULT_CHAT_MODEL))
            .build();

        log.info("Get OpenAiChatModel with model {}.", options.getModel());
        log.info("Get OpenAiChatModel with apiKey {}.", SensitiveUtils.apiKey(options.getApiKey()));
        log.info("Get OpenAiChatModel with baseUrl {}.", options.getBaseUrl());

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
            .openAiClient(this.openAiClient(config))
            .openAiClientAsync(this.openAiClientAsync(config))
            .options(options)
            .observationRegistry(this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        this.observationConvention.ifAvailable(chatModel::setObservationConvention);

        return chatModel;
    }

    private OpenAIClient openAiClient(ModelConfig config) {
        log.info("Get OpenAIClient for OpenAiChatModel");
        return OpenAiSetup.setupSyncClient(config.getBaseUrl(), config.getApiKey(), null,
            null, null,
            null, false, false,
            config.getName(), config.getTimeout(), config.getMaxRetries(), config.getProxy(),
            config.getHeaders(),
            this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            this.meterRegistry.getIfAvailable(),
            this.httpClientBuilderCustomizers.orderedStream().toList());
    }

    private OpenAIClientAsync openAiClientAsync(ModelConfig config) {
        log.info("Get OpenAIClientAsync for OpenAiChatModel");
        return OpenAiSetup.setupAsyncClient(config.getBaseUrl(), config.getApiKey(), null,
            null, null,
            null, false, false,
            config.getName(), config.getTimeout(), config.getMaxRetries(), config.getProxy(),
            config.getHeaders(),
            this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            this.meterRegistry.getIfAvailable(),
            this.httpClientBuilderCustomizers.orderedStream().toList());
    }

}
