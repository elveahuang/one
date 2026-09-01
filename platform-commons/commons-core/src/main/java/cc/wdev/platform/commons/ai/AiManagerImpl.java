package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.advisor.SessionMetadataAdvisor;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.enums.AiVectorStoreType;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.SpeechModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.TranscriptionModelFactory;
import cc.wdev.platform.commons.ai.factory.chat.ChatModelFactory;
import cc.wdev.platform.commons.ai.factory.embedding.EmbeddingModelFactory;
import cc.wdev.platform.commons.ai.factory.vectorstore.VectorStoreFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class AiManagerImpl implements AiManager {

    private final List<ChatModelFactory> chatModelFactories = new ArrayList<>();

    private final List<EmbeddingModelFactory> embeddingModelFactories = new ArrayList<>();

    private final List<TranscriptionModelFactory> transcriptionModelFactories = new ArrayList<>();

    private final List<SpeechModelFactory> speechModelFactories = new ArrayList<>();

    private final AiConfig config;

    private final ObjectProvider<ToolCallbackResolver> toolCallbackResolver;

    private final ObjectProvider<SessionService> sessionService;

    private final ObjectProvider<ChatMemory> chatMemory;

    private final List<ModelFactory<?>> modelFactories;

    private final List<VectorStoreFactory> vectorStoreFactories;

    @PostConstruct
    public void init() {
        log.info("ModelFactory Registry");

        if (CollectionUtils.isNotEmpty(modelFactories)) {
            for (ModelFactory<?> factory : modelFactories) {
                if (factory instanceof ChatModelFactory modelFactory) {
                    this.chatModelFactories.add(modelFactory);
                }
                if (factory instanceof EmbeddingModelFactory modelFactory) {
                    this.embeddingModelFactories.add(modelFactory);
                }
                if (factory instanceof TranscriptionModelFactory modelFactory) {
                    this.transcriptionModelFactories.add(modelFactory);
                }
                if (factory instanceof SpeechModelFactory modelFactory) {
                    this.speechModelFactories.add(modelFactory);
                }
            }
        }

        log.info("ModelFactory. total : {}", modelFactories.size());
        log.info("ChatModelFactory. total : {}", chatModelFactories.size());
        log.info("EmbeddingModelFactory. total : {}", embeddingModelFactories.size());
        log.info("TranscriptionModelFactory. total : {}", transcriptionModelFactories.size());
        log.info("SpeechModelFactory. total : {}", speechModelFactories.size());
    }

    // ------------------------------------------------------------------------
    // 基础方法
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#getConfig()
     */
    @Override
    public AiConfig getConfig() {
        return this.config;
    }

    /**
     * @see AiManager#getSessionService()
     */
    @Override
    public SessionService getSessionService() {
        return this.sessionService.getIfAvailable(AiUtils::getSessionService);
    }

    /**
     * @see AiManager#getSessionMemoryAdvisor()
     */
    @Override
    public SessionMemoryAdvisor getSessionMemoryAdvisor() {
        return AiUtils.getSessionMemoryAdvisor(this.getSessionService());
    }

    /**
     * @see AiManager#getSessionMetadataAdvisor()
     */
    @Override
    public SessionMetadataAdvisor getSessionMetadataAdvisor() {
        return AiUtils.getSessionMetadataAdvisor(this.getSessionService());
    }

    /**
     * @see AiManager#getQuestionAnswerAdvisor()
     */
    @Override
    public QuestionAnswerAdvisor getQuestionAnswerAdvisor() {
        return AiUtils.getQuestionAnswerAdvisor(this.getVectorStore(), this.getConfig().getRetrieval());
    }

    /**
     * @see AiManager#getRetrievalAugmentationAdvisor()
     */
    @Override
    public RetrievalAugmentationAdvisor getRetrievalAugmentationAdvisor() {
        return AiUtils.getRetrievalAugmentationAdvisor(this.getVectorStore(), this.getConfig().getRetrieval());
    }

    /**
     * @see AiManager#getToolCallbackResolver()
     */
    @Override
    public ObjectProvider<ToolCallbackResolver> getToolCallbackResolver() {
        return this.toolCallbackResolver;
    }

    // ------------------------------------------------------------------------
    // 文本模型
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#getChatModelFactory()
     */
    @Override
    public ChatModelFactory getChatModelFactory() {
        return getChatModelFactory(AiServiceProvider.getChatFactoryProvider(config.getFactory().getText()));
    }

    /**
     * @see AiManager#getChatModelFactory()
     */
    @Override
    public ChatModelFactory getChatModelFactory(AiServiceProvider serviceProvider) {
        return this.chatModelFactories.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable ChatModelFactory."));
    }

    /**
     * @see AiManager#getChatModelFactory(ModelConfig)
     */
    @Override
    public ChatModelFactory getChatModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("ChatModelFactory - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(config.getModelProvider())) {
            throw new IllegalArgumentException("ChatModelFactory - modelProvider cannot be null");
        }
        if (config.getName() == null) {
            throw new IllegalArgumentException("ChatModelFactory - modelName cannot be null");
        }

        ChatModelFactory factory = getChatModelFactory(AiServiceProvider.getChatFactoryProvider(config.getServiceProvider()));
        if (factory.supports(config)) {
            return factory;
        }
        return getChatModelFactory();
    }

    /**
     * @see AiManager#getChatClient()
     */
    @Override
    public ChatClient getChatClient() {
        return this.getChatModelFactory().getChatClient();
    }

    /**
     * @see AiManager#getChatClient(ModelConfig)
     */
    @Override
    public ChatClient getChatClient(AiServiceProvider serviceProvider) {
        return this.getChatModelFactory(serviceProvider).getChatClient();
    }

    /**
     * @see AiManager#getChatClient(ModelConfig)
     */
    @Override
    public ChatClient getChatClient(ModelConfig config) {
        return this.getChatModelFactory(config).getChatClient();
    }

    /**
     * @see AiManager#getChatModel()
     */
    @Override
    public ChatModel getChatModel() {
        return this.getChatModelFactory().getChatModel();
    }

    /**
     * @see AiManager#getChatModel(AiServiceProvider)
     */
    @Override
    public ChatModel getChatModel(AiServiceProvider serviceProvider) {
        return this.getChatModelFactory(serviceProvider).getChatModel();
    }

    /**
     * @see AiManager#getChatModel(ModelConfig)
     */
    @Override
    public ChatModel getChatModel(ModelConfig config) {
        return this.getChatModelFactory(config).getChatModel(config);
    }

    // ------------------------------------------------------------------------
    // 向量模型
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#getEmbeddingModelFactory(ModelConfig)
     */
    @Override
    public EmbeddingModelFactory getEmbeddingModelFactory() {
        return getEmbeddingModelFactory(AiServiceProvider.getEmbeddingFactoryProvider(config.getFactory().getEmbedding()));
    }

    /**
     * @see AiManager#getEmbeddingModelFactory(ModelConfig)
     */
    @Override
    public EmbeddingModelFactory getEmbeddingModelFactory(AiServiceProvider serviceProvider) {
        return this.embeddingModelFactories.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable EmbeddingModelFactory."));
    }

    /**
     * @see AiManager#getEmbeddingModelFactory(ModelConfig)
     */
    @Override
    public EmbeddingModelFactory getEmbeddingModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("EmbeddingModelFactory - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(config.getModelProvider())) {
            throw new IllegalArgumentException("EmbeddingModelFactory - modelProvider cannot be null");
        }
        if (config.getName() == null) {
            throw new IllegalArgumentException("EmbeddingModelFactory - modelName cannot be null");
        }

        EmbeddingModelFactory factory = this.getEmbeddingModelFactory(AiServiceProvider.getEmbeddingFactoryProvider(config.getServiceProvider()));
        if (factory.supports(config)) {
            return factory;
        }
        return getEmbeddingModelFactory();
    }

    /**
     * @see AiManager#getEmbeddingModel()
     */
    @Override
    public EmbeddingModel getEmbeddingModel() {
        return this.getEmbeddingModelFactory().getEmbeddingModel();
    }

    /**
     * @see AiManager#getEmbeddingModel(AiServiceProvider)
     */
    @Override
    public EmbeddingModel getEmbeddingModel(AiServiceProvider serviceProvider) {
        return this.getEmbeddingModelFactory(serviceProvider).getEmbeddingModel();
    }

    /**
     * @see AiManager#getEmbeddingModel(ModelConfig)
     */
    @Override
    public EmbeddingModel getEmbeddingModel(ModelConfig config) {
        return this.getEmbeddingModelFactory(config).getEmbeddingModel(config);
    }

    // ------------------------------------------------------------------------
    // 转录模型
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#getTranscriptionModelFactory()
     */
    @Override
    public TranscriptionModelFactory getTranscriptionModelFactory() {
        return getTranscriptionModelFactory(AiServiceProvider.getTranscriptionFactoryProvider(config.getFactory().getTranscription()));
    }

    /**
     * @see AiManager#getTranscriptionModelFactory(AiServiceProvider)
     */
    @Override
    public TranscriptionModelFactory getTranscriptionModelFactory(AiServiceProvider serviceProvider) {
        return this.transcriptionModelFactories.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable TranscriptionModelFactory."));
    }

    /**
     * @see AiManager#getTranscriptionModelFactory(ModelConfig)
     */
    @Override
    public TranscriptionModelFactory getTranscriptionModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("TranscriptionModelFactory - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(config.getModelProvider())) {
            throw new IllegalArgumentException("TranscriptionModelFactory - modelProvider cannot be null");
        }
        if (config.getName() == null) {
            throw new IllegalArgumentException("TranscriptionModelFactory - modelName cannot be null");
        }

        TranscriptionModelFactory factory = this.getTranscriptionModelFactory(AiServiceProvider.getTranscriptionFactoryProvider(config.getServiceProvider()));
        if (factory.supports(config)) {
            return factory;
        }
        return getTranscriptionModelFactory();
    }

    // ------------------------------------------------------------------------
    // 语音模型
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#getSpeechModelFactory()
     */
    @Override
    public SpeechModelFactory getSpeechModelFactory() {
        return getSpeechModelFactory(AiServiceProvider.getSpeechFactoryProvider(config.getFactory().getTranscription()));
    }

    /**
     * @see AiManager#getSpeechModelFactory(AiServiceProvider)
     */
    @Override
    public SpeechModelFactory getSpeechModelFactory(AiServiceProvider serviceProvider) {
        return this.speechModelFactories.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable SpeechModelFactory."));
    }

    /**
     * @see AiManager#getSpeechModelFactory(ModelConfig)
     */
    @Override
    public SpeechModelFactory getSpeechModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("SpeechModelFactory - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(config.getModelProvider())) {
            throw new IllegalArgumentException("SpeechModelFactory - modelProvider cannot be null");
        }
        if (config.getName() == null) {
            throw new IllegalArgumentException("SpeechModelFactory - modelName cannot be null");
        }

        SpeechModelFactory factory = this.getSpeechModelFactory(AiServiceProvider.getSpeechFactoryProvider(config.getServiceProvider()));
        if (factory.supports(config)) {
            return factory;
        }
        return getSpeechModelFactory();
    }

    // ------------------------------------------------------------------------
    // Vector Store & RAG
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#getVectorStoreFactory()
     */
    @Override
    public VectorStoreFactory getVectorStoreFactory() {
        AiVectorStoreType vectorStoreType = BaseEnum.getEnumByValue(
            this.config.getVectorStore().getType(), AiVectorStoreType.class, AiVectorStoreType.ELASTICSEARCH);
        return this.getVectorStoreFactory(vectorStoreType);
    }

    /**
     * @see AiManager#getVectorStoreFactory(AiVectorStoreType)
     */
    @Override
    public VectorStoreFactory getVectorStoreFactory(AiVectorStoreType vectorStoreType) {
        return this.vectorStoreFactories.stream()
            .filter(factory -> factory.getStoreType().equals(vectorStoreType))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable VectorStoreFactory: " + vectorStoreType));
    }

    /**
     * @see AiManager#getVectorStore()
     */
    @Override
    public VectorStore getVectorStore() {
        return this.getVectorStoreFactory().getVectorStore(this.getEmbeddingModel());
    }

    /**
     * @see AiManager#getVectorStore(String)
     */
    @Override
    public VectorStore getVectorStore(String collectionName) {
        return this.getVectorStoreFactory().getVectorStore(this.getEmbeddingModel(), collectionName);
    }

    /**
     * @see AiManager#getVectorStore(AiServiceProvider)
     */
    @Override
    public VectorStore getVectorStore(AiServiceProvider serviceProvider) {
        return this.getVectorStoreFactory().getVectorStore(this.getEmbeddingModel(serviceProvider));
    }

    /**
     * @see AiManager#getVectorStore(AiServiceProvider, String)
     */
    @Override
    public VectorStore getVectorStore(AiServiceProvider serviceProvider, String collectionName) {
        return this.getVectorStoreFactory().getVectorStore(this.getEmbeddingModel(serviceProvider), collectionName);
    }

    /**
     * @see AiManager#getVectorStore(ModelConfig)
     */
    @Override
    public VectorStore getVectorStore(ModelConfig config) {
        return this.getVectorStoreFactory().getVectorStore(this.getEmbeddingModel(config));
    }

    /**
     * @see AiManager#getVectorStore(ModelConfig, String)
     */
    @Override
    public VectorStore getVectorStore(ModelConfig config, String collectionName) {
        return this.getVectorStoreFactory().getVectorStore(this.getEmbeddingModel(config), collectionName);
    }

    /**
     * @see AiManager#getDocumentTransformer()
     */
    @Override
    public TextSplitter getDocumentTransformer() {
        return AiUtils.getDocumentTransformer(this.config.getSplitting());
    }

}
