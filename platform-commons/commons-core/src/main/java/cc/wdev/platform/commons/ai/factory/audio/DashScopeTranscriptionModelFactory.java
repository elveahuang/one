package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelTranscriptionConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import io.github.agentic.spring.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionModel;
import io.github.agentic.spring.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.retry.RetryTemplate;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildTranscriptionModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DashScopeTranscriptionModelFactory extends AbstractTranscriptionModelFactory {

    private final ObjectProvider<RetryTemplate> retryTemplate;

    public DashScopeTranscriptionModelFactory(ModelCommonsConfig commonsConfig,
                                              ModelTranscriptionConfig modelConfig,
                                              ObjectProvider<RetryTemplate> retryTemplate
    ) {
        super(buildTranscriptionModelConfig(commonsConfig, modelConfig));

        this.retryTemplate = retryTemplate;
    }

    /**
     * @see ModelFactory#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.AGENTIC_SPRING_AI;
    }

    /**
     * @see ModelFactory#getModel(ModelConfig)
     */
    @Override
    public DashScopeSdkAudioTranscriptionModel getModel(ModelConfig config) {
        DashScopeSdkAudioTranscriptionOptions options = DashScopeSdkAudioTranscriptionOptions.builder()
            .model(nvl(config.getName(), DashScopeSdkAudioTranscriptionModel.DEFAULT_MODEL_NAME))
            .build();

        return DashScopeSdkAudioTranscriptionModel.builder()
            .apiKey(config.getApiKey())
            .workspaceId(config.getWorkspaceId())
            .connectionHeaders(config.getHeaders())
            .defaultOptions(options)
            .retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
            .build();
    }

}
