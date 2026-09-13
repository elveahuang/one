package cc.wdev.platform.commons.autoconfigure.ai;

import cc.wdev.platform.commons.ai.AiConfig;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.AiManagerImpl;
import cc.wdev.platform.commons.ai.config.ModelProviderConfig;
import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.ai.config.SplittingConfig;
import cc.wdev.platform.commons.ai.enums.AiModelProvider;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.DashScopeTranscriptionModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.OpenAiTranscriptionModelFactory;
import cc.wdev.platform.commons.ai.factory.chat.AnthropicChatModelFactory;
import cc.wdev.platform.commons.ai.factory.chat.DashScopeChatModelFactory;
import cc.wdev.platform.commons.ai.factory.chat.DeepSeekChatModelFactory;
import cc.wdev.platform.commons.ai.factory.chat.OpenAiChatModelFactory;
import cc.wdev.platform.commons.ai.factory.embedding.OpenAiEmbeddingModelFactory;
import cc.wdev.platform.commons.ai.factory.image.OpenAiImageModelFactory;
import cc.wdev.platform.commons.ai.factory.vectorstore.ElasticsearchVectorStoreFactory;
import cc.wdev.platform.commons.ai.factory.vectorstore.MariaDBVectorStoreFactory;
import cc.wdev.platform.commons.ai.factory.vectorstore.PgVectorStoreFactory;
import cc.wdev.platform.commons.ai.factory.vectorstore.VectorStoreFactory;
import cc.wdev.platform.commons.ai.tools.CommonTools;
import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import cc.wdev.platform.commons.ai.ui.UiComponentManager;
import cc.wdev.platform.commons.ai.ui.UiComponentRegistry;
import cc.wdev.platform.commons.ai.ui.components.TextComponentDefinition;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiProperties;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiVectorStoreElasticsearchProperties;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiVectorStoreMariaDBProperties;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiVectorStorePgVectorProperties;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import com.google.common.collect.Maps;
import io.github.agentic.spring.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionModel;
import io.github.agentic.spring.ai.dashscope.sdk.chat.DashScopeSdkChatModel;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.http.okhttp.AnthropicHttpClientBuilderCustomizer;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.image.observation.ImageModelObservationConvention;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.mariadb.MariaDBVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = AiProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({
    AiProperties.class,
    AiVectorStoreElasticsearchProperties.class, AiVectorStorePgVectorProperties.class, AiVectorStoreMariaDBProperties.class
})
@ImportRuntimeHints(AiAutoConfiguration.AiRuntimeHints.class)
public class AiAutoConfiguration {

    public AiAutoConfiguration() {
        log.info("AiAutoConfiguration is enabled");
    }

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public AiConfig aiConfig(AiProperties properties) {
        AiConfig.AiConfigBuilder builder = AiConfig.builder()
            .fallbackEnabled(properties.isFallbackEnabled())
            .service(properties.getService())
            .factory(properties.getFactory())
            .vectorStore(properties.getVectorstore())
            .splitting(AiUtils.resolveSplittingConfig(SplittingConfig.builder().build(), properties.getSplitting()))
            .retrieval(AiUtils.resolveRetrievalConfig(RetrievalConfig.builder().build(), properties.getRetrieval()))
            .vectorization(properties.getVectorization())
            .agent(properties.getAgent())
            .memory(properties.getMemory());

        // 统一模型供应商的标识转为小写
        if (CollectionUtils.isNotEmpty(properties.getProviders())) {
            Map<String, ModelProviderConfig> providers = Maps.newHashMap();
            properties.getProviders().forEach((key, value) -> {
                providers.put(key.toLowerCase(), value);
            });
            builder.providers(providers);
        }

        return builder.build();
    }

    // ------------------------------------------------------------------------------
    // Chat Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DeepSeekChatModel.class)
    public DeepSeekChatModelFactory deepSeekChatModelFactory(
        AiConfig config,
        ObjectProvider<RetryTemplate> retryTemplate,
        ObjectProvider<ResponseErrorHandler> responseErrorHandler,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getChatModelProvider()).toLowerCase(), AiModelProvider.DEEPSEEK.name().toLowerCase())
        );
        return new DeepSeekChatModelFactory(providerConfig.getCommons(), providerConfig.getChat(),
            retryTemplate, responseErrorHandler, observationRegistry, observationConvention);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OpenAiChatModel.class)
    public OpenAiChatModelFactory openAiChatModelFactory(
        AiConfig config,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getChatModelProvider()).toLowerCase(), AiModelProvider.OPENAI.name().toLowerCase())
        );
        return new OpenAiChatModelFactory(providerConfig.getCommons(), providerConfig.getChat(),
            observationRegistry, meterRegistry, observationConvention, httpClientBuilderCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(AnthropicChatModel.class)
    public AnthropicChatModelFactory anthropicChatModelFactory(
        AiConfig config,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention,
        ObjectProvider<AnthropicHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getChatModelProvider()).toLowerCase(), AiModelProvider.ANTHROPIC.name().toLowerCase())
        );
        return new AnthropicChatModelFactory(providerConfig.getCommons(), providerConfig.getChat(),
            observationRegistry, meterRegistry, observationConvention, httpClientBuilderCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DashScopeSdkChatModel.class)
    public DashScopeChatModelFactory dashScopeChatModelFactory(
        AiConfig config,
        ObjectProvider<RetryTemplate> retryTemplate,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getChatModelProvider()).toLowerCase(), AiModelProvider.ALIYUN.name().toLowerCase())
        );
        return new DashScopeChatModelFactory(providerConfig.getCommons(), providerConfig.getChat(),
            retryTemplate, observationRegistry, observationConvention);
    }

    // ------------------------------------------------------------------------------
    // Audio Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OpenAiAudioTranscriptionModel.class)
    public OpenAiTranscriptionModelFactory openAiAudioModelFactory(
        AiConfig config,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getTranscriptionModelProvider()).toLowerCase(), AiModelProvider.OPENAI.name().toLowerCase())
        );
        return new OpenAiTranscriptionModelFactory(providerConfig.getCommons(), providerConfig.getTranscription(),
            observationRegistry, meterRegistry, httpClientBuilderCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DashScopeSdkAudioTranscriptionModel.class)
    public DashScopeTranscriptionModelFactory dashScopeTranscriptionModelFactory(
        AiConfig config,
        ObjectProvider<RetryTemplate> retryTemplate
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getTranscriptionModelProvider()).toLowerCase(), AiModelProvider.ALIYUN.name().toLowerCase())
        );
        return new DashScopeTranscriptionModelFactory(providerConfig.getCommons(), providerConfig.getTranscription(), retryTemplate);
    }

    // ------------------------------------------------------------------------------
    // Embeddings Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OpenAiEmbeddingModel.class)
    public OpenAiEmbeddingModelFactory openAiEmbeddingModelFactory(
        AiConfig config,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<EmbeddingModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getEmbeddingModelProvider()).toLowerCase(), AiModelProvider.OPENAI.name().toLowerCase())
        );
        return new OpenAiEmbeddingModelFactory(providerConfig.getCommons(), providerConfig.getEmbedding(),
            observationRegistry, meterRegistry, observationConvention, httpClientBuilderCustomizers);
    }

    // ------------------------------------------------------------------------------
    // Image Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OpenAiImageModel.class)
    public OpenAiImageModelFactory openAiImageModelFactory(
        AiConfig config,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<ImageModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        ModelProviderConfig providerConfig = AiUtils.resolveModelProviderConfig(config, StringUtils.nvl(
            StringUtils.nvl(config.getFactory().getImageModelProvider()).toLowerCase(), AiModelProvider.OPENAI.name().toLowerCase())
        );
        return new OpenAiImageModelFactory(providerConfig.getCommons(), providerConfig.getImage(),
            observationRegistry, meterRegistry, observationConvention, httpClientBuilderCustomizers);
    }

    // ----------------------------------------------------------------------
    // Vector Store
    // ----------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public BatchingStrategy batchingStrategy() {
        return new TokenCountBatchingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(ElasticsearchVectorStore.class)
    public ElasticsearchVectorStoreFactory elasticsearchVectorStoreFactory(
        ObjectProvider<Rest5Client> restClientProvider,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<VectorStoreObservationConvention> convention,
        BatchingStrategy batchingStrategy,
        AiVectorStoreElasticsearchProperties config
    ) {
        log.info("Creating ElasticsearchVectorStoreFactory");

        return new ElasticsearchVectorStoreFactory(restClientProvider, observationRegistry,
            convention, batchingStrategy, config);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(MariaDBVectorStore.class)
    @ConditionalOnProperty(prefix = AiVectorStoreMariaDBProperties.PREFIX, name = "enabled", havingValue = "true")
    public MariaDBVectorStoreFactory mariaDBVectorStoreFactory(
        ObjectProvider<JdbcTemplate> jdbcTemplateProvider,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<VectorStoreObservationConvention> convention,
        BatchingStrategy batchingStrategy,
        AiVectorStoreMariaDBProperties config
    ) {
        log.info("Creating MariaDBStoreFactory");

        return new MariaDBVectorStoreFactory(jdbcTemplateProvider, observationRegistry,
            convention, batchingStrategy, config);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(PgVectorStore.class)
    @ConditionalOnProperty(prefix = AiVectorStorePgVectorProperties.PREFIX, name = "enabled", havingValue = "true")
    public PgVectorStoreFactory pgVectorStoreFactory(
        ObjectProvider<JdbcTemplate> jdbcTemplateProvider,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<VectorStoreObservationConvention> convention,
        BatchingStrategy batchingStrategy,
        AiVectorStorePgVectorProperties config
    ) {
        log.info("Creating pgVectorStoreFactory");

        return new PgVectorStoreFactory(jdbcTemplateProvider, observationRegistry,
            convention, batchingStrategy, config);
    }

    // ------------------------------------------------------------------------------
    // AI Manager
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public AiManager aiManager(AiConfig config,
                               ObjectProvider<ToolCallbackResolver> toolCallbackResolver,
                               ObjectProvider<SessionService> sessionService,
                               List<ModelFactory<?>> modelFactories,
                               List<VectorStoreFactory> vectorStoreFactories) {
        return new AiManagerImpl(config, toolCallbackResolver, sessionService,
            modelFactories, vectorStoreFactories
        );
    }

    // ------------------------------------------------------------------------------
    // Tools & Others
    // ------------------------------------------------------------------------------

    @Bean
    public CommonTools commonTools() {
        return new CommonTools();
    }

    @Bean
    public MethodToolCallbackProvider toolCallbackProvider(CommonTools commonTools) {
        return MethodToolCallbackProvider.builder().toolObjects(commonTools).build();
    }

    @Bean
    public UiComponentRegistry uiComponentRegistry(List<UiComponentDefinition> definitions) {
        UiComponentRegistry registry = new UiComponentRegistry();
        registry.register(definitions);
        registry.register(new TextComponentDefinition());

        UiComponentManager.setRegistry(registry);

        return registry;
    }

    // ------------------------------------------------------------------------------
    // AOT
    // ------------------------------------------------------------------------------

    public static class AiRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
            log.info("Register RuntimeHints by AiRuntimeHints");
            var mcs = MemberCategory.values();
            for (var type : Set.of(CommonTools.class)) {
                hints.reflection().registerType(type, mcs);
            }
        }
    }

}
