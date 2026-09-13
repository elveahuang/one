package cc.wdev.platform.commons.autoconfigure.ai;

import cc.wdev.platform.commons.ai.AiConfig;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.AiServiceManagerImpl;
import cc.wdev.platform.commons.ai.config.ModelProviderConfig;
import cc.wdev.platform.commons.ai.enums.AiModelProvider;
import cc.wdev.platform.commons.ai.service.ModelService;
import cc.wdev.platform.commons.ai.service.audio.DashScopeTranscriptionModelService;
import cc.wdev.platform.commons.ai.service.audio.HunYuanTranscriptionModelService;
import cc.wdev.platform.commons.ai.service.chat.OpenAiChatModelService;
import cc.wdev.platform.commons.ai.service.embedding.DashScopeEmbeddingModelService;
import cc.wdev.platform.commons.ai.service.image.DashScopeImageModelService;
import cc.wdev.platform.commons.ai.service.image.HunYuanImageModelService;
import cc.wdev.platform.commons.ai.service.rerank.DashScopeRerankModelService;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiProperties;
import cc.wdev.platform.commons.utils.StringUtils;
import com.alibaba.dashscope.rerank.TextReRank;
import com.openai.client.OpenAIClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration(after = AiAutoConfiguration.class)
@ConditionalOnProperty(prefix = AiProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({AiProperties.class})
public class AiServiceAutoConfiguration {

    private final AiConfig config;

    public AiServiceAutoConfiguration(AiConfig config) {
        log.info("AiServiceAutoConfiguration is enabled");

        this.config = config;
    }

    // ------------------------------------------------------------------------------
    // Chat Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OpenAIClient.class)
    public OpenAiChatModelService openAiChatModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getChatModelProvider(), AiModelProvider.OPENAI.name().toLowerCase())
        );
        return new OpenAiChatModelService(providerConfig.getCommons(), providerConfig.getChat());
    }

    // ------------------------------------------------------------------------------
    // Transcription Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public DashScopeTranscriptionModelService dashScopeTranscriptionModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getTranscriptionModelProvider(), AiModelProvider.ALIYUN.name().toLowerCase())
        );
        return new DashScopeTranscriptionModelService(providerConfig.getCommons(), providerConfig.getTranscription());
    }

    @Bean
    @ConditionalOnMissingBean
    public HunYuanTranscriptionModelService hunYuanTranscriptionModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getTranscriptionModelProvider(), AiModelProvider.TENCENT.name().toLowerCase())
        );
        return new HunYuanTranscriptionModelService(providerConfig.getCommons(), providerConfig.getTranscription());
    }

    // ------------------------------------------------------------------------------
    // Embedding Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public DashScopeEmbeddingModelService dashScopeEmbeddingModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getEmbeddingModelProvider(), AiModelProvider.ALIYUN.name().toLowerCase())
        );
        return new DashScopeEmbeddingModelService(providerConfig.getCommons(), providerConfig.getEmbedding());
    }

    // ------------------------------------------------------------------------------
    // Image Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public DashScopeImageModelService dashScopeImageModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getImageModelProvider(), AiModelProvider.ALIYUN.name().toLowerCase())
        );
        return new DashScopeImageModelService(providerConfig.getCommons(), providerConfig.getImage());
    }

    @Bean
    @ConditionalOnMissingBean
    public HunYuanImageModelService hunYuanImageModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getImageModelProvider(), AiModelProvider.TENCENT.name().toLowerCase())
        );
        return new HunYuanImageModelService(providerConfig.getCommons(), providerConfig.getImage());
    }

    // ------------------------------------------------------------------------------
    // Rerank Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(TextReRank.class)
    public DashScopeRerankModelService dashScopeRerankModelService(AiConfig config) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            config.getService().getRerankModelProvider(), AiModelProvider.ALIYUN.name().toLowerCase())
        );
        return new DashScopeRerankModelService(providerConfig.getCommons(), providerConfig.getRerank());
    }

    // ------------------------------------------------------------------------------
    // AI Service Manager
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(AiServiceManager.class)
    public AiServiceManager aiServiceManager(List<ModelService> modelServices) {
        return new AiServiceManagerImpl(config, modelServices);
    }

}
