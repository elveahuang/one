package cc.wdev.platform.commons.ai.utils;

import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.commons.ai.advisor.CustomContextAdvisor;
import cc.wdev.platform.commons.ai.advisor.CustomLoggingAdvisor;
import cc.wdev.platform.commons.ai.advisor.SessionMetadataAdvisor;
import cc.wdev.platform.commons.ai.config.*;
import cc.wdev.platform.commons.ai.domain.chat.SimpleChatContent;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiContentType;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.SpringUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.session.compaction.SlidingWindowCompactionStrategy;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static cc.wdev.platform.commons.ai.AiConstants.*;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
public abstract class AiUtils {

    public static final SimpleChatContent STREAM_CONTENT_START = SimpleChatContent.builder().type(AiContentType.START.getValue()).build();

    public static final SimpleChatContent STREAM_CONTENT_END = SimpleChatContent.builder().type(AiContentType.END.getValue()).build();

    public static final SimpleChatContent STREAM_CONTENT_ERROR = SimpleChatContent.builder().type(AiContentType.ERROR.getValue()).build();

    public static final String END_LINE = "\n\n";

    public static final String JSON_RENDER_START_TAG = "```json-render";

    public static final String JSON_RENDER_END_TAG = "```";

    /**
     * 生成新的对话ID
     */
    public static String generateConversationId() {
        return StringUtils.uuid();
    }

    /**
     * 处理提示词
     */
    public static String renderPrompt(String prompt, Map<String, Object> context) {
        if (MapUtils.isNotEmpty(context)) {
            PromptTemplate promptTemplate = PromptTemplate
                .builder()
                .template(prompt)
                .variables(context)
                .build();
            return promptTemplate.render();
        }
        return prompt;
    }

    // ------------------------------------------------------------------------------
    // Stream Content
    // ------------------------------------------------------------------------------

    public static String getStartContent() {
        return GsonUtils.toJson(STREAM_CONTENT_START);
    }

    public static String getEndContent() {
        return GsonUtils.toJson(STREAM_CONTENT_END);
    }

    public static String getErrorContent() {
        return GsonUtils.toJson(STREAM_CONTENT_ERROR);
    }

    /**
     * Text Block
     */
    public static String getTextContent(String text) {
        return GsonUtils.toJson(SimpleChatContent.builder().type(AiContentType.TEXT.getValue()).content(text).build());
    }

    /**
     * Json Render Block
     */
    public static String getJsonRenderContent(String text) {
        String builder = JSON_RENDER_START_TAG + END_LINE + text + END_LINE + JSON_RENDER_END_TAG + END_LINE;
        return GsonUtils.toJson(SimpleChatContent.builder().type(AiContentType.TEXT.getValue()).content(builder).build());
    }

    /**
     * Citation Block（引用溯源）
     */
    public static String getCitationContent(List<cc.wdev.platform.commons.ai.domain.chat.SimpleCitation> citations) {
        return GsonUtils.toJson(SimpleChatContent.builder()
            .type(AiContentType.CITATION.getValue())
            .citations(citations)
            .build());
    }

    /**
     * 检测当前缓冲区是否包含开始标记
     * 包含开始标记，则发送标记前的内容，并开启交互模式
     * 未包含开始标记，则发送标记前的内容，这个时候需要保留一个安全的长度，避免截断开始标记
     */
    public static Flux<String> processStream(Flux<@NotNull String> rawStream) {
        return Flux.defer(() -> {
            AtomicBoolean interaction = new AtomicBoolean(false);

            StringBuilder buffer = new StringBuilder();
            return rawStream.handle((String data, SynchronousSink<String> sink) -> {
                buffer.append(data);

                int idx;
                String remaining = buffer.toString();
                if (!interaction.get()) {
                    if (remaining.contains(JSON_RENDER_START_TAG)) {
                        // 内容包含开始标记，那么发送标记前的内容，并开启交互模式，等待结束标记
                        idx = remaining.indexOf(JSON_RENDER_START_TAG);
                        String text = remaining.substring(0, idx);
                        if (StringUtils.isNotEmpty(text)) {
                            sink.next(getTextContent(text));
                        }

                        idx = remaining.indexOf(JSON_RENDER_START_TAG) + JSON_RENDER_START_TAG.length();
                        buffer.delete(0, idx);

                        interaction.set(true);
                    } else {
                        if (remaining.length() > (JSON_RENDER_START_TAG.length() + 10)) {
                            // 保留一个安全区域，防止开始标记被截断，影响页面渲染效果
                            idx = remaining.length() - JSON_RENDER_START_TAG.length();
                            sink.next(getTextContent(remaining.substring(0, idx)));
                            buffer.delete(0, idx);
                        }
                    }
                } else {
                    // 内容包含结束标记，截取开始标记到结束标记之间的内容，发送交互内容
                    if (remaining.contains(JSON_RENDER_END_TAG)) {
                        idx = remaining.indexOf(JSON_RENDER_END_TAG);
                        String text = buffer.substring(0, idx);
                        if (StringUtils.isNotEmpty(text)) {
                            sink.next(getJsonRenderContent(text));
                        }

                        idx = remaining.indexOf(JSON_RENDER_END_TAG) + JSON_RENDER_END_TAG.length();
                        buffer.delete(0, idx);

                        interaction.set(false);
                    }
                }
            }).concatWith(Flux.defer(() -> {
                if (!buffer.isEmpty()) {
                    return Flux.just(getTextContent(buffer.toString()));
                }
                return Flux.empty();
            }));
        });
    }

    // ------------------------------------------------------------------------------
    // ChatClient
    // ------------------------------------------------------------------------------

    public static ChatResponse chatCompletion(ChatClient chatClient, SimpleChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = processChatSpec(chatClient, request);
        return spec.call().chatResponse();
    }

    public static ChatResponse chatCompletion(ChatClient chatClient, Prompt prompt) {
        return chatClient.prompt(prompt).call().chatResponse();
    }

    public static String chatCompletionText(ChatClient chatClient, SimpleChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = processChatSpec(chatClient, request);
        return spec.call().content();
    }

    public static Flux<String> streamChatCompletionText(ChatClient chatClient, SimpleChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = processChatSpec(chatClient, request);
        return spec.stream().content();
    }

    public static Flux<ChatResponse> streamChatCompletion(ChatClient chatClient, SimpleChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = processChatSpec(chatClient, request);
        return spec.stream().chatResponse();
    }

    public static Flux<ChatResponse> streamChatCompletion(ChatClient chatClient, Prompt prompt) {
        return chatClient.prompt(prompt).stream().chatResponse();
    }

    // ------------------------------------------------------------------------------
    // Rag
    // ------------------------------------------------------------------------------

    public static QuestionAnswerAdvisor getQuestionAnswerAdvisor(VectorStore vectorStore) {
        return QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
            .build();
    }

    // ------------------------------------------------------------------------------
    // Utils
    // ------------------------------------------------------------------------------

    public static ChatMemory getChatMemory() {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .maxMessages(AiConstants.MAX_MEMORY_MESSAGE_COUNT)
            .build();
    }

    public static MessageChatMemoryAdvisor getMessageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).scheduler(MessageChatMemoryAdvisor.DEFAULT_SCHEDULER).build();
    }

    public static CustomContextAdvisor getCustomContextAdvisor() {
        return new CustomContextAdvisor();
    }

    public static CustomLoggingAdvisor getCustomLoggingAdvisor() {
        return new CustomLoggingAdvisor();
    }


    public static SessionService getSessionService() {
        return DefaultSessionService.builder()
            .sessionRepository(InMemorySessionRepository.builder().build())
            .build();
    }

    public static SessionMemoryAdvisor getSessionMemoryAdvisor(SessionService sessionService) {
        return SessionMemoryAdvisor.builder(sessionService)
            .compactionTrigger(new TurnCountTrigger(20))
            .compactionStrategy(SlidingWindowCompactionStrategy.builder().maxEvents(10).build())
            .build();
    }

    public static SessionMetadataAdvisor getSessionMetadataAdvisor(SessionService sessionService) {
        return new SessionMetadataAdvisor(sessionService);
    }

    public static QuestionAnswerAdvisor getQuestionAnswerAdvisor(VectorStore vectorStore, RetrievalConfig config) {
        return QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder()
                .similarityThreshold(config.getSimilarityThreshold())
                .topK(config.getTopK())
                .build()
            ).build();
    }

    public static RetrievalAugmentationAdvisor getRetrievalAugmentationAdvisor(VectorStore vectorStore, RetrievalConfig config) {
        return RetrievalAugmentationAdvisor.builder()
            .documentRetriever(VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(config.getSimilarityThreshold())
                .topK(config.getTopK())
                .build()
            ).queryAugmenter(ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build()
            ).build();
    }

    public static TextSplitter getDocumentTransformer(SplittingConfig config) {
        return TokenTextSplitter.builder()
            .withChunkSize(config.getChunkSize())
            .build();
    }

    public static DocumentRetriever getDocumentRetriever(VectorStore vectorStore,
                                                         RetrievalConfig retrievalConfig,
                                                         Filter.Expression filterExpression) {
        return VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .topK(retrievalConfig.getTopK())
            .similarityThreshold(retrievalConfig.getSimilarityThreshold())
            .filterExpression(filterExpression)
            .build();
    }

    public static ChatClient.ChatClientRequestSpec processChatSpec(ChatClient chatClient, SimpleChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt().advisors(a -> {
            a.param(CAHT_CONTEXT_SESSION_ID_KEY, request.getConversationId());
            a.param(CAHT_CONTEXT_USER_ID_KEY, String.valueOf(request.getUserId()));
            a.param(CAHT_CONTEXT_TENANT_ID_KEY, request.getTenantId());
        }).user(u -> {
            u.text(request.getPrompt());

            u.metadata(METADATA_SESSION_ID, request.getConversationId());
            u.metadata(METADATA_TENANT_ID, request.getTenantId());
            u.metadata(METADATA_USER_ID, String.valueOf(request.getUserId()));
            u.metadata(METADATA_CHAT_TYPE, request.getChatType());
            u.metadata(METADATA_AGENT_CODE, StringUtils.nvl(request.getAgentCode()));
        });

        // Tool Calling
        List<ToolCallback> tools = getToolObject(request.getToolNames());
        if (CollectionUtils.isNotEmpty(tools)) {
            spec.tools(tools);
        }
        // 工具上下文（无条件注入，供 KnowledgeTools 等工具通过 ToolContext 读取）
        Map<String, Object> toolContext = new HashMap<>();
        toolContext.put(AiConstants.METADATA_USER_ID, request.getUserId());
        toolContext.put(AiConstants.METADATA_TENANT_ID, request.getTenantId());
        if (request.getKbId() != null) {
            toolContext.put(AiConstants.TOOL_CONTEXT_KB_ID, request.getKbId());
        }
        spec.toolContext(toolContext);
        // 系统提示词
        if (StringUtils.isNotEmpty(request.getSystemPrompt())) {
            spec = spec.system(request.getSystemPrompt());
        }
        // 温度参数
        if (request.getTemperature() != null && request.getTemperature() > 0) {
            spec = spec.options(ChatOptions.builder().temperature(request.getTemperature().doubleValue()));
        }
        return spec;
    }

    public static @Nullable String getChatResponseContent(ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse)
            .map(ChatResponse::getResult)
            .map(Generation::getOutput)
            .map(AbstractMessage::getText)
            .orElse(null);
    }

    @NonNull
    public static List<ToolCallback> getToolObject(List<String> toolNames) {
        if (CollectionUtils.isEmpty(toolNames)) {
            return Collections.emptyList();
        }

        List<ToolCallback> objects = Lists.newArrayList();
        SpringUtils.getBeanProvider(ToolCallbackResolver.class).ifAvailable(resolver -> {
            for (String toolName : toolNames) {
                ToolCallback object = resolver.resolve(toolName);
                if (object != null) {
                    objects.add(object);
                }
            }
        });
        return objects;
    }

    // ------------------------------------------------------------------------------
    // Config
    // ------------------------------------------------------------------------------

    public static ModelConfig buildChatModelConfig(ModelCommonsConfig parentConfig, ModelChatConfig modelConfig) {
        String baseUrl = nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = nvl(modelConfig.getName(), "");
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static ModelConfig buildTranscriptionModelConfig(ModelCommonsConfig parentConfig, ModelTranscriptionConfig modelConfig) {
        String baseUrl = nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = nvl(modelConfig.getName(), "");
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static ModelConfig buildSpeechModelConfig(ModelCommonsConfig parentConfig, ModelSpeechConfig modelConfig) {
        String baseUrl = nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = nvl(modelConfig.getName(), "");
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static ModelConfig buildEmbeddingModelConfig(ModelCommonsConfig parentConfig, ModelEmbeddingConfig modelConfig) {
        String baseUrl = nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = nvl(modelConfig.getName(), "");
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static ModelConfig buildRerankModelConfig(ModelCommonsConfig parentConfig, ModelRerankConfig modelConfig) {
        String baseUrl = nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = nvl(modelConfig.getName(), "");
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static ModelConfig buildImageModelConfig(ModelCommonsConfig parentConfig, ModelImageConfig modelConfig) {
        String baseUrl = nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = nvl(modelConfig.getName(), "");
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static SplittingConfig resolveSplittingConfig(@NonNull SplittingConfig defaultConfig, @NonNull SplittingConfig config) {
        SplittingConfig.SplittingConfigBuilder builder = SplittingConfig.builder();
        builder.strategy(nvl(config.getStrategy(), defaultConfig.getStrategy()));
        builder.chunkSize(nvl(config.getChunkSize(), defaultConfig.getChunkSize()));
        builder.chunkOverlap(nvl(config.getChunkOverlap(), defaultConfig.getChunkOverlap()));
        return builder.build();
    }

    public static RetrievalConfig resolveRetrievalConfig(@NonNull RetrievalConfig defaultConfig, @NonNull RetrievalConfig config) {
        RetrievalConfig.RetrievalConfigBuilder builder = RetrievalConfig.builder();
        builder.topK(nvl(config.getTopK(), defaultConfig.getTopK()));
        builder.similarityThreshold(nvl(config.getSimilarityThreshold(), defaultConfig.getSimilarityThreshold()));
        return builder.build();
    }

}
