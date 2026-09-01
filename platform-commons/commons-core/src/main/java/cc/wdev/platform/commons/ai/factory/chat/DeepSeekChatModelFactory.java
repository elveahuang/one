package cc.wdev.platform.commons.ai.factory.chat;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.utils.StringUtils;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildChatModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DeepSeekChatModelFactory extends AbstractChatModelFactory {

    private final ObjectProvider<RetryTemplate> retryTemplate;
    private final ObjectProvider<ResponseErrorHandler> responseErrorHandler;
    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<ChatModelObservationConvention> observationConvention;

    public DeepSeekChatModelFactory(ModelCommonsConfig commonsConfig, ModelChatConfig modelConfig,
                                    ObjectProvider<RetryTemplate> retryTemplate,
                                    ObjectProvider<ResponseErrorHandler> responseErrorHandler,
                                    ObjectProvider<ObservationRegistry> observationRegistry,
                                    ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        super(buildChatModelConfig(commonsConfig, modelConfig));

        this.retryTemplate = retryTemplate;
        this.responseErrorHandler = responseErrorHandler;
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    /**
     * @see ModelFactory#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.SPRING_AI_DEEPSEEK;
    }

    /**
     * @see ChatModelFactory#getModel(ModelConfig)
     */
    @Override
    public DeepSeekChatModel getModel(ModelConfig config) {
        DeepSeekApi.Builder apiBuilder = DeepSeekApi.builder().apiKey(config.getApiKey());
        if (StringUtils.isNotEmpty(config.getBaseUrl())) {
            apiBuilder.baseUrl(config.getBaseUrl());
        }
        apiBuilder.restClientBuilder(RestClient.builder());
        apiBuilder.webClientBuilder(WebClient.builder());
        apiBuilder.responseErrorHandler(this.responseErrorHandler.getIfAvailable(() -> RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER));

        DeepSeekChatOptions.Builder optionsBuilder = DeepSeekChatOptions.builder();
        optionsBuilder.model(nvl(config.getName(), DeepSeekApi.ChatModel.DEEPSEEK_V4_FLASH.getValue()));

        DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
            .deepSeekApi(apiBuilder.build())
            .options(optionsBuilder.build())
            .retryTemplate(this.retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
            .observationRegistry(this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        this.observationConvention.ifAvailable(chatModel::setObservationConvention);

        return chatModel;
    }

}
