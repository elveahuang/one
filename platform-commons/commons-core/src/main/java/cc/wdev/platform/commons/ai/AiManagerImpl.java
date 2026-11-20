package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.config.AgentConfig;
import cc.wdev.platform.commons.ai.config.MemoryConfig;
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
import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springaicommunity.agent.advisors.AutoMemoryToolsAdvisor;
import org.springaicommunity.agent.common.task.subagent.SubagentType;
import org.springaicommunity.agent.dream.AutoDreamAdvisor;
import org.springaicommunity.agent.dream.AutoDreamService;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.task.TaskTool;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentReferences;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
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
     * @see AiManager#getSessionAdvisor()
     */
    @Override
    public List<Advisor> getSessionAdvisor() {
        return AiUtils.getSessionAdvisor(this.getSessionService());
    }

    /**
     * @see AiManager#getQuestionAnswerAdvisor()
     */
    @Override
    public QuestionAnswerAdvisor getQuestionAnswerAdvisor() {
        return AiUtils.getQuestionAnswerAdvisor(this.getVectorStore(), this.getConfig().getRag().getRetrieval());
    }

    /**
     * @see AiManager#getRetrievalAugmentationAdvisor()
     */
    @Override
    public RetrievalAugmentationAdvisor getRetrievalAugmentationAdvisor() {
        // 默认检索链路必须带租户隔离过滤，防止跨租户召回
        return AiUtils.getRetrievalAugmentationAdvisor(
            this.getVectorStore(), this.getConfig().getRag().getRetrieval(), AiUtils.buildTenantScopeFilter());
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
        AiServiceProvider serviceProvider = AiServiceProvider.getChatFactoryProvider(this.config.getFactory().getChatServiceProvider());

        return this.chatModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable ChatModelFactory."));
    }

    /**
     * @see AiManager#getChatModelFactory()
     */
    @Override
    public ChatModelFactory getChatModelFactory(AiServiceProvider serviceProvider) {
        ChatModelFactory factory = this.chatModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElse(null);

        if (factory != null) {
            return factory;
        } else if (this.getConfig().isFallbackEnabled()) {
            return this.getChatModelFactory();
        } else {
            throw new RuntimeException("Unavailable ChatModelFactory.");
        }
    }

    /**
     * @see AiManager#getChatModelFactory(ModelConfig)
     */
    @Override
    public ChatModelFactory getChatModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("ChatModelFactory - serviceProvider cannot be null");
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
        return this.getChatModelFactory(config).getChatClient(config);
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
        AiServiceProvider serviceProvider = AiServiceProvider.getEmbeddingFactoryProvider(this.config.getFactory().getEmbeddingServiceProvider());

        return this.embeddingModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable EmbeddingModelFactory."));
    }

    /**
     * @see AiManager#getEmbeddingModelFactory(ModelConfig)
     */
    @Override
    public EmbeddingModelFactory getEmbeddingModelFactory(AiServiceProvider serviceProvider) {
        EmbeddingModelFactory factory = this.embeddingModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElse(null);

        if (factory != null) {
            return factory;
        } else if (this.getConfig().isFallbackEnabled()) {
            return this.getEmbeddingModelFactory();
        } else {
            throw new RuntimeException("Unavailable EmbeddingModelFactory.");
        }
    }

    /**
     * @see AiManager#getEmbeddingModelFactory(ModelConfig)
     */
    @Override
    public EmbeddingModelFactory getEmbeddingModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("EmbeddingModelFactory - serviceProvider cannot be null");
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
        AiServiceProvider serviceProvider = AiServiceProvider.getTranscriptionFactoryProvider(this.config.getFactory().getTranscriptionServiceProvider());

        return this.transcriptionModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable TranscriptionModelFactory."));
    }

    /**
     * @see AiManager#getTranscriptionModelFactory(AiServiceProvider)
     */
    @Override
    public TranscriptionModelFactory getTranscriptionModelFactory(AiServiceProvider serviceProvider) {
        TranscriptionModelFactory factory = this.transcriptionModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElse(null);

        if (factory != null) {
            return factory;
        } else if (this.getConfig().isFallbackEnabled()) {
            return this.getTranscriptionModelFactory();
        } else {
            throw new RuntimeException("Unavailable TranscriptionModelFactory.");
        }
    }

    /**
     * @see AiManager#getTranscriptionModelFactory(ModelConfig)
     */
    @Override
    public TranscriptionModelFactory getTranscriptionModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("TranscriptionModelFactory - serviceProvider cannot be null");
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
        AiServiceProvider serviceProvider = AiServiceProvider.getSpeechFactoryProvider(this.config.getFactory().getSpeechServiceProvider());

        return this.speechModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable SpeechModelFactory."));
    }

    /**
     * @see AiManager#getSpeechModelFactory(AiServiceProvider)
     */
    @Override
    public SpeechModelFactory getSpeechModelFactory(AiServiceProvider serviceProvider) {
        SpeechModelFactory factory = this.speechModelFactories.stream()
            .filter(f -> {
                AiServiceProvider provider = f.getServiceProvider();
                return provider.isEnabled() && provider.equals(serviceProvider);
            })
            .findFirst()
            .orElse(null);

        if (factory != null) {
            return factory;
        } else if (this.getConfig().isFallbackEnabled()) {
            return this.getSpeechModelFactory();
        } else {
            throw new RuntimeException("Unavailable SpeechModelFactory.");
        }
    }

    /**
     * @see AiManager#getSpeechModelFactory(ModelConfig)
     */
    @Override
    public SpeechModelFactory getSpeechModelFactory(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("SpeechModelFactory - serviceProvider cannot be null");
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
            this.config.getRag().getStore().getType(), AiVectorStoreType.class, AiVectorStoreType.ELASTICSEARCH);
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
        return AiUtils.getDocumentTransformer(this.config.getRag().getSplitting());
    }

    // ------------------------------------------------------------------------
    // Agent Utils
    // ------------------------------------------------------------------------

    /**
     * @see AiManager#applyAgentTool(ChatClient.Builder)
     */
    @Override
    public void applyAgentTool(ChatClient.Builder builder) {
        this.applyAgentTool(builder, this.getConfig().getAgent());
    }

    /**
     * @see AiManager#applyAgentTool(ChatClient.Builder, AgentConfig)
     */
    @Override
    public void applyAgentTool(ChatClient.Builder builder, AgentConfig config) {
        List<Resource> skillsResources = this.resolveDirectories(config.getSkills(), "skills");
        List<Resource> agentsResources = this.resolveDirectories(config.getAgents(), "agents");

        // 主智能体加载能力前先克隆，避免主智能体和子智能体重复加载
        ChatClient.Builder subAgentBuilder = builder.clone();

        if (config.isEnabled()) {
            // SkillsTool
            if (CollectionUtils.isNotEmpty(skillsResources)) {
                builder.defaultTools(SkillsTool.builder().addSkillsResources(skillsResources).build());
            }

            // TaskTool
            if (CollectionUtils.isNotEmpty(agentsResources)) {
                // SubAgent
                if (CollectionUtils.isNotEmpty(skillsResources)) {
                    SubagentType claudeType = ClaudeSubagentType.builder()
                        .chatClientBuilder("default", subAgentBuilder)
                        .skillsResources(skillsResources)
                        .build();

                    builder.defaultTools(TaskTool.builder()
                        .subagentReferences(ClaudeSubagentReferences.fromResources(agentsResources))
                        .subagentTypes(claudeType)
                        .build()
                    );
                }
            }
        }
    }

    /**
     * @see AiManager#applyMemoryAdvisor(ChatClient.Builder, ChatClient.Builder)
     */
    @Override
    public void applyMemoryAdvisor(ChatClient.Builder builder, ChatClient.Builder dreamBuilder) {
        this.applyMemoryAdvisor(builder, dreamBuilder, this.getConfig().getMemory());
    }

    /**
     * @see AiManager#applyMemoryAdvisor(ChatClient.Builder, ChatClient.Builder, MemoryConfig)
     */
    @Override
    public void applyMemoryAdvisor(ChatClient.Builder builder, ChatClient.Builder dreamBuilder, MemoryConfig config) {
        if (config.isEnabled() && StringUtils.isNotEmpty(config.getPath())) {
            // AutoMemoryToolsAdvisor
            String path = Paths.get(config.getPath())
                .resolve(String.valueOf(SecurityUtils.getTid()))
                .resolve(String.valueOf(SecurityUtils.getUid()))
                .normalize()
                .toString();
            log.info("Apply memory directory: {}", path);

            builder.defaultAdvisors(AutoMemoryToolsAdvisor.builder()
                .memoriesRootDirectory(path)
                .build());

            // AutoDreamAdvisor
            // 长期记忆整理只需要一个干净的模型总结，无需带上工具和能力，减少词元消耗
            AutoDreamService autoDreamService = AutoDreamService.builder(dreamBuilder).build();
            builder.defaultAdvisors(AutoDreamAdvisor.builder()
                .memoriesRootDirectory(path)
                .dreamService(autoDreamService)
                .build());
        }
    }

    /**
     * @see AiManager#applyBaseAdvisors(ChatClient.Builder)
     */
    @Override
    public void applyBaseAdvisors(ChatClient.Builder builder) {
        builder.defaultAdvisors(AiUtils.getCustomLoggingAdvisor());
        builder.defaultAdvisors(AiUtils.getCustomContextAdvisor());
        builder.defaultAdvisors(AiUtils.getSessionAdvisor(this.getSessionService()));
    }

    /**
     * @see AiManager#applyBaseAdvisors(ChatClient.Builder)
     */
    @Override
    public void applyTools(ChatClient.Builder builder, final List<String> toolNames) {
        if (CollectionUtils.isNotEmpty(toolNames)) {
            List<ToolCallback> objects = Lists.newArrayList();
            this.getToolCallbackResolver().ifAvailable(resolver -> {
                for (String toolName : toolNames) {
                    ToolCallback object = resolver.resolve(toolName);
                    if (object != null) {
                        objects.add(object);
                    }
                }
            });
            builder.defaultTools(objects);
        }
    }

    /**
     * @see AiManager#applyRagAdvisors(ChatClient.Builder)
     */
    @Override
    public void applyRagAdvisors(ChatClient.Builder builder) {
        builder.defaultAdvisors(this.getRetrievalAugmentationAdvisor());
    }

    /**
     * @see AiManager#loadDefaultPrompt()
     */
    @Override
    public String loadDefaultPrompt() {
        return this.loadDefaultPrompt(AiConstants.DEFAULT_PROMPT_NAME, AiConstants.DEFAULT_PROMPT);
    }

    /**
     * @see AiManager#loadDefaultPrompt(String, String)
     */
    @Override
    public String loadDefaultPrompt(String name, String fallbackPrompt) {
        Resource directory = this.getConfig().getAgent().getPrompts();
        if (directory != null && directory.exists()) {
            try {
                Path directoryPath = Paths.get(directory.getFile().getAbsolutePath()).normalize();
                Path promptPath = directoryPath.resolve(this.resolvePromptFileName(name)).normalize();
                Resource resource = new FileSystemResource(promptPath.toFile());
                if (resource.exists() && resource.isReadable()) {
                    String content = resource.getContentAsString(GlobalConstants.UTF8);
                    if (StringUtils.isNotEmpty(content)) {
                        return content;
                    }
                    log.warn("Prompt file [{}] is empty, fallback to default prompt.", name);
                } else {
                    log.warn("Prompt file [{}] not found under [{}], fallback to default prompt.", name, directoryPath);
                }
            } catch (Exception e) {
                log.error("Fail to load prompt file [{}] from [{}].", name, directory.getDescription(), e);
            }
        }
        return fallbackPrompt;
    }

    // -----------------------------------------------------------------------------------------------
    // 私有辅助方法
    // -----------------------------------------------------------------------------------------------

    /**
     * 规范提示词文件名：仅保留最后一段文件名（禁止携带路径），缺省补 .md 扩展名
     */
    private String resolvePromptFileName(String name) {
        String fileName = StringUtils.nvl(name, "").trim().replace("\\", "/");
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        if (StringUtils.isEmpty(fileName) || ".".equals(fileName)) {
            return "";
        }
        return fileName.endsWith(".md") ? fileName : fileName + ".md";
    }

    /**
     * 解析技能和子智能体目录资源
     */
    private List<Resource> resolveDirectories(List<Resource> resources, String dirName) {
        List<Resource> resolved = Lists.newArrayList();
        for (Resource resource : CollectionUtils.nvl(resources)) {
            if (resource.exists()) {
                log.info("Add {} directory: {}", dirName, resource.getDescription());
                resolved.add(resource);
                continue;
            }
            log.info("{} directory {} not exists, fallback to working directory", dirName, resource.getDescription());
        }
        return resolved;
    }

}
