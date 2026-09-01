package cc.wdev.platform.commons.ai.factory.chat;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import io.github.agentic.spring.ai.dashscope.sdk.chat.DashScopeSdkChatModel;
import io.github.agentic.spring.ai.dashscope.sdk.chat.DashScopeSdkChatOptions;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.retry.RetryTemplate;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildChatModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DashScopeChatModelFactory extends AbstractChatModelFactory {

    private final ObjectProvider<RetryTemplate> retryTemplate;
    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final ObjectProvider<ChatModelObservationConvention> observationConvention;

    public DashScopeChatModelFactory(ModelCommonsConfig commonsConfig, ModelChatConfig modelConfig,
                                     ObjectProvider<RetryTemplate> retryTemplate,
                                     ObjectProvider<ObservationRegistry> observationRegistry,
                                     ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        super(buildChatModelConfig(commonsConfig, modelConfig));

        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    /**
     * @see ModelFactory#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.AGENTIC_SPRING_AI;
    }

    /**
     * @see ChatModelFactory#getModel(ModelConfig)
     */
    @Override
    public DashScopeSdkChatModel getModel(ModelConfig config) {
        DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
            .model(nvl(config.getName(), DashScopeSdkChatModel.DEFAULT_MODEL_NAME))
            .build();

        DashScopeSdkChatModel chatModel = DashScopeSdkChatModel.builder()
            .apiKey(config.getApiKey())
            .workspaceId(config.getWorkspaceId())
            .connectionHeaders(config.getHeaders())
            .defaultOptions(options)
            .retryTemplate(this.retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
            .observationRegistry(this.observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        this.observationConvention.ifAvailable(chatModel::setObservationConvention);
        return chatModel;
    }

}
