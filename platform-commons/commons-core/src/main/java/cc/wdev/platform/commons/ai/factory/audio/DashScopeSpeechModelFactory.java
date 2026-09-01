package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelSpeechConfig;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import io.github.agentic.spring.ai.dashscope.sdk.audio.tts.DashScopeSdkAudioSpeechModel;
import io.github.agentic.spring.ai.dashscope.sdk.audio.tts.DashScopeSdkAudioSpeechOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.retry.RetryTemplate;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildSpeechModelConfig;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DashScopeSpeechModelFactory extends AbstractSpeechModelFactory {
    private final ObjectProvider<RetryTemplate> retryTemplate;

    public DashScopeSpeechModelFactory(ModelCommonsConfig commonsConfig,
                                       ModelSpeechConfig modelConfig,
                                       ObjectProvider<RetryTemplate> retryTemplate
    ) {
        super(buildSpeechModelConfig(commonsConfig, modelConfig));

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
    public DashScopeSdkAudioSpeechModel getModel(ModelConfig config) {
        DashScopeSdkAudioSpeechOptions options = DashScopeSdkAudioSpeechOptions.builder()
            .model(nvl(config.getName(), DashScopeSdkAudioSpeechModel.DEFAULT_MODEL_NAME))
            .build();

        return DashScopeSdkAudioSpeechModel.builder()
            .apiKey(config.getApiKey())
            .workspaceId(config.getWorkspaceId())
            .connectionHeaders(config.getHeaders())
            .defaultOptions(options)
            .retryTemplate(this.retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
            .build();
    }

}
