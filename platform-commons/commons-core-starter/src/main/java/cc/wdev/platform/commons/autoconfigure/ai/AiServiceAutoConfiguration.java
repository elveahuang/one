package cc.wdev.platform.commons.autoconfigure.ai;

import cc.wdev.platform.commons.ai.AiConfig;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.AiServiceManagerImpl;
import cc.wdev.platform.commons.ai.service.ModelService;
import cc.wdev.platform.commons.ai.service.audio.DashScopeTranscriptionModelService;
import cc.wdev.platform.commons.ai.service.audio.HunYuanTranscriptionModelService;
import cc.wdev.platform.commons.ai.service.chat.OpenAiChatModelService;
import cc.wdev.platform.commons.ai.service.embedding.DashScopeEmbeddingModelService;
import cc.wdev.platform.commons.ai.service.image.DashScopeImageModelService;
import cc.wdev.platform.commons.ai.service.image.HunYuanImageModelService;
import cc.wdev.platform.commons.ai.service.rerank.DashScopeRerankModelService;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiAliyunProperties;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiOpenAiProperties;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiProperties;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiTencentProperties;
import com.alibaba.dashscope.common.DashScopeResult;
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
    @ConditionalOnClass(DashScopeResult.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_OPENAI, name = "enabled", havingValue = "true", matchIfMissing = true)
    public OpenAiChatModelService openAiChatModelService(AiOpenAiProperties properties) {
        return new OpenAiChatModelService(properties.getCommons(), properties.getChat());
    }

    // ------------------------------------------------------------------------------
    // Audio Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(DashScopeTranscriptionModelService.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_ALIYUN, name = "enabled", havingValue = "true", matchIfMissing = true)
    public DashScopeTranscriptionModelService dashScopeTranscriptionModelService(AiAliyunProperties properties) {
        return new DashScopeTranscriptionModelService(properties.getCommons(), properties.getTranscription());
    }

    @Bean
    @ConditionalOnMissingBean(RerankModelService.class)
    @ConditionalOnClass(com.alibaba.dashscope.rerank.TextReRank.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_ALIYUN, name = "enabled", havingValue = "true", matchIfMissing = true)
    public DashScopeRerankModelService dashScopeRerankModelService(AiAliyunProperties properties) {
        return new DashScopeRerankModelService(properties.getCommons(), properties.getRerank());
    }

    @Bean
    @ConditionalOnMissingBean(HunYuanTranscriptionModelService.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_TENCENT, name = "enabled", havingValue = "true", matchIfMissing = true)
    public HunYuanTranscriptionModelService hunYuanTranscriptionModelService(AiTencentProperties properties) {
        return new HunYuanTranscriptionModelService(properties.getCommons(), properties.getTranscription());
    }

    // ------------------------------------------------------------------------------
    // Embedding Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(DashScopeEmbeddingModelService.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_ALIYUN, name = "enabled", havingValue = "true", matchIfMissing = true)
    public DashScopeEmbeddingModelService dashScopeEmbeddingModelService(AiAliyunProperties properties) {
        return new DashScopeEmbeddingModelService(properties.getCommons(), properties.getEmbedding());
    }

    // ------------------------------------------------------------------------------
    // Image Service
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(DashScopeImageModelService.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_ALIYUN, name = "enabled", havingValue = "true", matchIfMissing = true)
    public DashScopeImageModelService dashScopeImageModelService(AiAliyunProperties properties) {
        return new DashScopeImageModelService(properties.getCommons(), properties.getImage());
    }

    @Bean
    @ConditionalOnMissingBean(HunYuanImageModelService.class)
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_TENCENT, name = "enabled", havingValue = "true", matchIfMissing = true)
    public HunYuanImageModelService hunYuanImageModelService(AiTencentProperties properties) {
        return new HunYuanImageModelService(properties.getCommons(), properties.getImage());
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
