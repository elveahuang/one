package cc.wdev.platform.commons.ai.factory.chat;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.AnthropicClientAsync;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.AnthropicSetup;
import org.springframework.ai.anthropic.http.okhttp.AnthropicHttpClientBuilderCustomizer;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.beans.factory.ObjectProvider;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildChatModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class AnthropicChatModelFactory extends AbstractChatModelFactory {

    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<MeterRegistry> meterRegistry;
    private final ObjectProvider<ChatModelObservationConvention> observationConvention;
    private final ObjectProvider<AnthropicHttpClientBuilderCustomizer> httpClientBuilderCustomizers;

    public AnthropicChatModelFactory(ModelCommonsConfig commonsConfig, ModelChatConfig modelConfig,
                                     ObjectProvider<ObservationRegistry> observationRegistry,
                                     ObjectProvider<MeterRegistry> meterRegistry,
                                     ObjectProvider<ChatModelObservationConvention> observationConvention,
                                     ObjectProvider<AnthropicHttpClientBuilderCustomizer> httpClientBuilderCustomizers) {
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
        return AiServiceProvider.SPRING_AI_ANTHROPIC;
    }

    /**
     * @see ChatModelFactory#getModel(ModelConfig)
     */
    @Override
    public AnthropicChatModel getModel(ModelConfig config) {
        AnthropicChatOptions options = AnthropicChatOptions.builder()
            .model(nvl(config.getName(), AnthropicChatOptions.DEFAULT_MODEL))
            .build();

        log.info("Get AnthropicChatModel with model {}.", options.getModel());
        log.info("Get AnthropicChatModel with apiKey {}.", SensitiveUtils.apiKey(options.getApiKey()));
        log.info("Get AnthropicChatModel with baseUrl {}.", options.getBaseUrl());

        AnthropicChatModel chatModel = AnthropicChatModel.builder()
            .anthropicClient(this.anthropicClient(config))
            .anthropicClientAsync(this.anthropicClientAsync(config))
            .options(options)
            .observationRegistry(this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        this.observationConvention.ifAvailable(chatModel::setObservationConvention);

        return chatModel;
    }

    private AnthropicClient anthropicClient(ModelConfig config) {
        log.info("Get AnthropicClient for AnthropicChatModel");
        return AnthropicSetup.setupSyncClient(config.getBaseUrl(), config.getApiKey(),
            config.getTimeout(), config.getMaxRetries(), config.getProxy(),
            config.getHeaders(),
            this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            this.meterRegistry.getIfAvailable(),
            null,
            this.httpClientBuilderCustomizers.orderedStream().toList());
    }

    private AnthropicClientAsync anthropicClientAsync(ModelConfig config) {
        log.info("Get AnthropicClientAsync for AnthropicChatModel");
        return AnthropicSetup.setupAsyncClient(config.getBaseUrl(), config.getApiKey(),
            config.getTimeout(), config.getMaxRetries(), config.getProxy(),
            config.getHeaders(),
            this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            this.meterRegistry.getIfAvailable(),
            null,
            this.httpClientBuilderCustomizers.orderedStream().toList());
    }

}
