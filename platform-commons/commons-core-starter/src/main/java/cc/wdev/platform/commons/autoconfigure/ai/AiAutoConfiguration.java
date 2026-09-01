package cc.wdev.platform.commons.autoconfigure.ai;

import cc.wdev.platform.commons.ai.AiConfig;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.AiManagerImpl;
import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.ai.config.ServiceProviderConfig;
import cc.wdev.platform.commons.ai.config.SplittingConfig;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.DashScopeTranscriptionModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.OpenAiTranscriptionModelFactory;
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
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.autoconfigure.ai.properties.*;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import io.github.agentic.spring.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionModel;
import io.github.agentic.spring.ai.dashscope.sdk.chat.DashScopeSdkChatModel;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.memory.ChatMemory;
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
import java.util.Set;

import static cc.wdev.platform.commons.ai.enums.AiServiceProvider.*;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = AiProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({
    AiProperties.class, AiTokenTextSplitterProperties.class,
    AiVectorStoreElasticsearchProperties.class, AiVectorStorePgVectorProperties.class, AiVectorStoreMariaDBProperties.class,
    AiDeepSeekProperties.class, AiAliyunProperties.class, AiTencentProperties.class, AiOpenAiProperties.class, AiOrcaRouterProperties.class
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
        return AiConfig.builder().service(ServiceProviderConfig.builder()
                .text(getChatServiceProvider(properties.getService().getText()).getValue())
                .embedding(getEmbeddingServiceProvider(properties.getService().getEmbedding()).getValue())
                .image(getImageServiceProvider(properties.getService().getImage()).getValue())
                .speech(getSpeechServiceProvider(properties.getService().getSpeech()).getValue())
                .transcription(getTranscriptionServiceProvider(properties.getService().getTranscription()).getValue())
                .rerank(getRerankServiceProvider(properties.getService().getRerank()).getValue())
                .build())
            .factory(ServiceProviderConfig.builder()
                .text(getChatFactoryProvider(properties.getFactory().getText()).getValue())
                .embedding(getEmbeddingFactoryProvider(properties.getFactory().getEmbedding()).getValue())
                .image(getImageFactoryProvider(properties.getFactory().getImage()).getValue())
                .speech(getSpeechFactoryProvider(properties.getFactory().getSpeech()).getValue())
                .transcription(getTranscriptionFactoryProvider(properties.getFactory().getSpeech()).getValue())
                .build())
            .vectorStore(properties.getVectorstore())
            .splitting(AiUtils.resolveSplittingConfig(SplittingConfig.builder().build(), properties.getSplitting()))
            .retrieval(AiUtils.resolveRetrievalConfig(RetrievalConfig.builder().build(), properties.getRetrieval()))
            .vectorization(properties.getVectorization())
            .workspace(properties.getWorkspace())
            .skill(properties.getSkill())
            .build();
    }

    // ------------------------------------------------------------------------------
    // Chat Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_DEEPSEEK, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(DeepSeekChatModel.class)
    public DeepSeekChatModelFactory deepSeekChatModelFactory(
        AiDeepSeekProperties properties,
        ObjectProvider<RetryTemplate> retryTemplate,
        ObjectProvider<ResponseErrorHandler> responseErrorHandler,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        return new DeepSeekChatModelFactory(
            properties.getCommons(), properties.getChat(),
            retryTemplate, responseErrorHandler, observationRegistry, observationConvention);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_OPENAI, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(OpenAiChatModel.class)
    public OpenAiChatModelFactory openAiChatModelFactory(
        AiOpenAiProperties properties,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        return new OpenAiChatModelFactory(
            properties.getCommons(), properties.getChat(),
            observationRegistry, meterRegistry, observationConvention, httpClientBuilderCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_ALIYUN, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(DashScopeSdkChatModel.class)
    public DashScopeChatModelFactory dashScopeChatModelFactory(
        AiAliyunProperties properties,
        ObjectProvider<RetryTemplate> retryTemplate,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        return new DashScopeChatModelFactory(
            properties.getCommons(), properties.getChat(),
            retryTemplate, observationRegistry, observationConvention);
    }

    // ------------------------------------------------------------------------------
    // Audio Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_OPENAI, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(OpenAiAudioTranscriptionModel.class)
    public OpenAiTranscriptionModelFactory openAiAudioModelFactory(
        AiOpenAiProperties properties,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        return new OpenAiTranscriptionModelFactory(properties.getCommons(), properties.getTranslation(),
            observationRegistry, meterRegistry, httpClientBuilderCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_ALIYUN, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(DashScopeSdkAudioTranscriptionModel.class)
    public DashScopeTranscriptionModelFactory dashScopeTranscriptionModelFactory(
        AiOpenAiProperties properties,
        ObjectProvider<RetryTemplate> retryTemplate
    ) {
        return new DashScopeTranscriptionModelFactory(properties.getCommons(), properties.getTranslation(), retryTemplate);
    }

    // ------------------------------------------------------------------------------
    // Embeddings Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_OPENAI, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(OpenAiEmbeddingModel.class)
    public OpenAiEmbeddingModelFactory openAiEmbeddingModelFactory(
        AiOpenAiProperties properties,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<EmbeddingModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        return new OpenAiEmbeddingModelFactory(properties.getCommons(), properties.getEmbedding(),
            observationRegistry, meterRegistry, observationConvention, httpClientBuilderCustomizers);
    }

    // ------------------------------------------------------------------------------
    // Image Model
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AiProperties.PROVIDER_OPENAI, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(OpenAiImageModel.class)
    public OpenAiImageModelFactory openAiImageModelFactory(
        AiOpenAiProperties properties,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<MeterRegistry> meterRegistry,
        ObjectProvider<ImageModelObservationConvention> observationConvention,
        ObjectProvider<OpenAiHttpClientBuilderCustomizer> httpClientBuilderCustomizers
    ) {
        return new OpenAiImageModelFactory(properties.getCommons(), properties.getImage(),
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
    @ConditionalOnProperty(prefix = AiVectorStoreElasticsearchProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
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
                               ObjectProvider<ChatMemory> chatMemory,
                               List<ModelFactory<?>> modelFactories,
                               List<VectorStoreFactory> vectorStoreFactories) {
        return new AiManagerImpl(config, toolCallbackResolver, sessionService, chatMemory,
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
