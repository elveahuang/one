package cc.wdev.platform.commons.ai.utils;

import cc.wdev.platform.commons.ai.AiConfig;
import cc.wdev.platform.commons.ai.advisor.CustomContextAdvisor;
import cc.wdev.platform.commons.ai.advisor.CustomLoggingAdvisor;
import cc.wdev.platform.commons.ai.config.*;
import cc.wdev.platform.commons.ai.domain.chat.SimpleChatContent;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.ai.enums.AiContentType;
import cc.wdev.platform.commons.ai.enums.AiResponseType;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.ui.UiBlock;
import cc.wdev.platform.commons.ai.ui.UiComponentManager;
import cc.wdev.platform.commons.ai.ui.UiOutputConverter;
import cc.wdev.platform.commons.ai.ui.UiResponse;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.EventFilter;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.session.compaction.WindowCompactionStrategy;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.ai.AiConstants.*;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public abstract class AiUtils {

    public static final SimpleChatContent STREAM_CONTENT_START = SimpleChatContent.builder().type(AiContentType.START.getValue()).build();

    public static final SimpleChatContent STREAM_CONTENT_END = SimpleChatContent.builder().type(AiContentType.END.getValue()).build();

    public static final SimpleChatContent STREAM_CONTENT_ERROR = SimpleChatContent.builder().type(AiContentType.ERROR.getValue()).build();

    public static final String END_LINE = "\n\n";

    public static final String JSON_RENDER_START_TAG = "```json-render";

    public static final String JSON_RENDER_END_TAG = "```";

    /**
     * 按响应模式生成交付形态说明
     */
    public static String processSystemPrompt(String prompt, String responseType) {
        if (AiResponseType.STRICT.getValue().equalsIgnoreCase(responseType)) {
            return prompt + END_LINE + """
                【当前响应模式】STRICT
                - 只能输出符合 UI Schema 的 blocks JSON，禁止输出 json-render 围栏，禁止输出 blocks 以外的任何文字。
                - 正文放在 {"type":"text","props":{"content":"<Markdown 正文>"}} 块里，卡片块按已加载 Skill 的卡片规范生成。""";
        } else if (AiResponseType.JSON.getValue().equalsIgnoreCase(responseType)) {
            return prompt + END_LINE + """
                【当前响应模式】JSON
                - 正文用 Markdown 输出，并在正文末尾输出 **唯一一个** json-render 围栏。
                - 卡片只能出现在围栏里，正文不得复述卡片清单。""";
        } else {
            return prompt + END_LINE + """
                【当前响应模式】TEXT
                - 只输出 Markdown 正文，禁止输出 json-render 围栏或任何卡片标记。
                - 需要展示课程、讲师等结构化信息时，用文字或 Markdown 列表说明。""";
        }
    }

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * 预处理请求
     * 1. 重要参数，比如租户和用户信息等，不管前端有没有传参数过来都直接覆盖
     * 2. 其他参数，前端没传参数过来，那么按预设的复制
     */
    public static void processChatRequest(SimpleChatRequest request) {
        request.setTenantId(null != request.getTenantId() && request.getTenantId() > 0 ? request.getTenantId() : SecurityUtils.getTid());
        request.setUserId(null != request.getUserId() && request.getUserId() > 0 ? request.getUserId() : SecurityUtils.getUid());
        request.setConversationId(StringUtils.nvl(request.getConversationId(), StringUtils.uuid()));
        request.setResponseType(StringUtils.nvl(request.getResponseType(), AiResponseType.TEXT.getValue()));
        request.setChatType(StringUtils.nvl(request.getChatType(), AiChatType.STATIC.getValue()));
        if (request.getAction() != null && StringUtils.isNotEmpty(request.getAction().getCode())) {
            String actionType = request.getAction().getType();
            String actionCode = request.getAction().getCode();
            if ("tool".equalsIgnoreCase(actionType)) {
                List<String> tools = request.getToolNames() != null ? new ArrayList<>(request.getToolNames()) : new ArrayList<>();
                if (!tools.contains(actionCode)) {
                    tools.add(actionCode);
                }
                request.setToolNames(tools);
            } else if ("agent".equalsIgnoreCase(actionType) && StringUtils.isEmpty(request.getAgentCode())) {
                request.setAgentCode(actionCode);
            }
        }
    }

    /**
     * 处理请求
     * 1. 请求上下文基础数据
     * 2. 大模型参数
     * 3. 提示词
     */
    public static ChatClient.ChatClientRequestSpec processChatSpec(ChatClient chatClient, SimpleChatRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put(CHAT_CONTEXT_SESSION, request.getConversationId());
        params.put(CHAT_CONTEXT_USER_ID, StringUtils.nvl(String.valueOf(SecurityUtils.getUid()), String.valueOf(request.getUserId())));
        params.put(CHAT_CONTEXT_TENANT_ID, request.getTenantId());
        params.put(CHAT_CONTEXT_CHAT_BIZ_TYPE, StringUtils.nvl(request.getBizType()));
        params.put(CHAT_CONTEXT_CHAT_TYPE, StringUtils.nvl(request.getChatType()));
        params.put(CHAT_CONTEXT_AGENT_CODE, StringUtils.nvl(request.getAgentCode()));
        params.put(CHAT_CONTEXT_MODEL_CODE, StringUtils.nvl(request.getModelCode()));
        params.put(CHAT_CONTEXT_KB_CODE, StringUtils.nvl(request.getKbCode()));
        // 合入业务扩展参数（如 externalInd）；固定上下文 key 优先，防止外部参数覆盖安全字段
        if (request.getParams() != null) {
            request.getParams().forEach(params::putIfAbsent);
        }

        ChatClient.ChatClientRequestSpec spec = chatClient.prompt().toolContext(params).advisors(a -> {
            a.params(params);
            a.param(CHAT_CONTEXT_SESSION_METADATA, params);
        }).system(s -> {
            if (StringUtils.isNotEmpty(request.getSystemPrompt())) {
                s.text(request.getSystemPrompt());
            }
            s.metadata(params);
        }).user(u -> {
            u.text(request.getPrompt());
            u.metadata(params);
        });
        // 温度参数
        if (request.getTemperature() != null && request.getTemperature() > 0) {
            spec = spec.options(ChatOptions.builder().temperature(request.getTemperature().doubleValue()));
        }
        return spec;
    }

    public static String processChatResponse(ChatClient.ChatClientRequestSpec spec, SimpleChatRequest request) {
        log.info("processChatResponse [{}] text", request.getConversationId());
        if (StringUtils.isNotEmpty(request.getResponseType()) && AiResponseType.STRICT.getValue().equalsIgnoreCase(request.getResponseType())) {
            // STRICT - 严格模式，响应的数据全部经过结构化输出校验，然后按类型分块输出
            log.info("processChatResponse [{}] strict", request.getConversationId());
            try {
                UiOutputConverter converter = UiComponentManager.getRegistry().getConverter();
                UiResponse response = spec.call().entity(converter, ChatClient.EntityParamSpec::validateSchema);
                return GsonUtils.toJson(response);
            } catch (Exception e) {
                log.error("processChatResponse [{}] error", request.getConversationId(), e);
                return AiUtils.getErrorContent();
            }
        } else {
            // 默认模式，响应数据由大模型直接按提示词约束生成
            log.info("processChatResponse [{}] text", request.getConversationId());
            return spec.call().content();
        }
    }

    public static Flux<String> processStreamChatResponse(ChatClient.ChatClientRequestSpec spec, SimpleChatRequest request) {
        if (StringUtils.isNotEmpty(request.getResponseType()) && AiResponseType.STRICT.getValue().equalsIgnoreCase(request.getResponseType())) {
            // STRICT - 严格模式，响应的数据全部经过结构化输出校验，然后按类型分块输出
            log.info("processStreamChatResponse [{}] block", request.getConversationId());
            try {
                UiOutputConverter converter = UiComponentManager.getRegistry().getConverter();
                UiResponse response = spec.call().entity(converter, ChatClient.EntityParamSpec::validateSchema);
                List<UiBlock> blocks = response != null ? response.blocks() : Collections.emptyList();
                Flux<String> flux = Flux.fromIterable(CollectionUtils.nvl(blocks)).map(AiUtils::getBlockContent);
                return Flux.concat(Mono.just(AiUtils.getStartContent()), flux, Mono.just(AiUtils.getEndContent()));
            } catch (Exception e) {
                log.error("processStreamChatResponse [{}] error", request.getConversationId(), e);
                return Flux.just(AiUtils.getErrorContent());
            }
        } else if (StringUtils.isNotEmpty(request.getResponseType()) && AiResponseType.JSON.getValue().equalsIgnoreCase(request.getResponseType())) {
            // 普通模式，响应数据由大模型直接按提示词约束生成，带事件定义
            log.info("processStreamChatResponse [{}] json", request.getConversationId());
            try {
                Flux<String> flux = AiUtils.processStream(spec.stream().content());
                return Flux.concat(Mono.just(AiUtils.getStartContent()), flux, Mono.just(AiUtils.getEndContent()));
            } catch (Exception e) {
                log.error("processStreamChatResponse [{}] error", request.getConversationId(), e);
                return Flux.just(AiUtils.getErrorContent());
            }
        } else {
            // 默认模式，响应数据由大模型直接按提示词约束生成，无事件定义
            log.info("processStreamChatResponse [{}] text", request.getConversationId());
            return spec.stream().content();
        }
    }

    public static @Nullable String getChatResponseContent(ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse)
            .map(ChatResponse::getResult)
            .map(Generation::getOutput)
            .map(AbstractMessage::getText)
            .orElse(null);
    }

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
        String builder = END_LINE + JSON_RENDER_START_TAG + END_LINE + text + END_LINE + JSON_RENDER_END_TAG + END_LINE;
        return GsonUtils.toJson(SimpleChatContent.builder().type(AiContentType.TEXT.getValue()).content(builder).build());
    }

    /**
     * UI Block
     */
    public static String getBlockContent(UiBlock block) {
        return GsonUtils.toJson(SimpleChatContent.builder()
            .type(AiContentType.BLOCK.getValue())
            .block(block)
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
            return rawStream.concatMap(data -> {
                buffer.append(data);
                List<String> outputs = new ArrayList<>();

                while (true) {
                    String remaining = buffer.toString();
                    if (!interaction.get()) {
                        int idx = remaining.indexOf(JSON_RENDER_START_TAG);
                        if (idx >= 0) {
                            // 内容包含开始标记，发送标记前的内容，并开启交互模式
                            String text = remaining.substring(0, idx);
                            if (StringUtils.isNotEmpty(text)) {
                                outputs.add(getTextContent(text));
                            }

                            int nextIdx = idx + JSON_RENDER_START_TAG.length();
                            buffer.delete(0, nextIdx);

                            interaction.set(true);
                            // 开启交互模式后继续循环，检查当前缓冲区是否已包含结束标记
                        } else {
                            // 仅保留可能是开始标记前缀的尾部，普通正文立即下发，
                            // 避免正文末尾被扣到工具提问卡片之后才发送
                            int keep = markerPrefixSuffixLength(remaining);
                            int safeIdx = remaining.length() - keep;
                            if (safeIdx > 0) {
                                outputs.add(getTextContent(remaining.substring(0, safeIdx)));
                                buffer.delete(0, safeIdx);
                            }
                            break;
                        }
                    } else {
                        // 内容包含结束标记，截取开始标记到结束标记之间的内容，发送交互内容
                        int idx = remaining.indexOf(JSON_RENDER_END_TAG);
                        if (idx >= 0) {
                            String text = remaining.substring(0, idx);
                            if (StringUtils.isNotEmpty(text.trim())) {
                                outputs.add(getJsonRenderContent(text.trim()));
                            }

                            int nextIdx = idx + JSON_RENDER_END_TAG.length();
                            buffer.delete(0, nextIdx);

                            interaction.set(false);
                            // 退出交互模式后继续循环，检查当前缓冲区是否还有后续正文或新标记
                        } else {
                            break;
                        }
                    }
                }
                return Flux.fromIterable(outputs);
            }).concatWith(Flux.defer(() -> {
                if (!buffer.isEmpty()) {
                    if (interaction.get()) {
                        log.warn("Stream completed before closing tag for json-render was received, recovering unclosed card.");
                        String text = buffer.toString().trim();
                        if (StringUtils.isNotEmpty(text)) {
                            return Flux.just(getJsonRenderContent(text));
                        }
                        return Flux.empty();
                    } else {
                        return Flux.just(getTextContent(buffer.toString()));
                    }
                }
                return Flux.empty();
            }));
        });
    }

    /**
     * 返回文本尾部最长、且恰好是 JSON_RENDER_START_TAG 前缀的长度。
     * 普通文本返回 0，可立即下发；只在开始标记可能被拆包时才保留少量字符。
     */
    private static int markerPrefixSuffixLength(String text) {
        int max = Math.min(text.length(), JSON_RENDER_START_TAG.length() - 1);
        for (int len = max; len > 0; len--) {
            if (text.regionMatches(text.length() - len, JSON_RENDER_START_TAG, 0, len)) {
                return len;
            }
        }
        return 0;
    }

    // ------------------------------------------------------------------------------
    // Tools & Advisors
    // ------------------------------------------------------------------------------

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

    public static List<Advisor> getSessionAdvisor(SessionService sessionService) {
        return SessionAdvisors.builder(sessionService)
            .compaction(new TurnCountTrigger(20), WindowCompactionStrategy.builder().lastTurns(10).build())
            .asyncCompaction(true)
            .memory(memory -> memory.eventFilter(EventFilter.lastN(50)))
            .build();
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
        PromptTemplate promptTemplate = new PromptTemplate("""
            以下是检索到的参考资料（供参考）：
            ---------------------
            {context}
            ---------------------
            回答规则：
            1. 如果上述参考资料中包含直接答案，请优先依据资料准确回答；
            2. 如果参考资料中未提供答案或不完整，或者用户提出的是操作、查询、调用工具等任务型指令，
               请结合你的通用能力、上下文，并**主动检查并调用可用的工具**来完成用户的请求；
            3. 不要生硬回复“我不知道”，请尽最大努力协助用户。
            用户问题：{query}
            """);

        return RetrievalAugmentationAdvisor.builder().documentRetriever(resilientDocumentRetriever(VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .similarityThreshold(config.getSimilarityThreshold())
            .topK(config.getTopK())
            .build()
        )).queryAugmenter(ContextualQueryAugmenter.builder()
            .promptTemplate(promptTemplate)
            .allowEmptyContext(Boolean.TRUE)
            .emptyContextPromptTemplate(PromptTemplate.builder().template("找不到相关的内容。").build())
            .documentFormatter(documents -> documents.stream().map(document -> {
                    String metadata = document.getMetadata().entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(", "));
                    return metadata + "\n" + document.getText();
                }).collect(Collectors.joining(System.lineSeparator()))
            ).build()
        ).build();
    }

    /**
     * 获取检索增强 Advisor（带过滤条件，租户隔离检索必须使用本重载）
     */
    public static RetrievalAugmentationAdvisor getRetrievalAugmentationAdvisor(VectorStore vectorStore,
                                                                               RetrievalConfig config,
                                                                               Filter.Expression filterExpression) {
        PromptTemplate promptTemplate = new PromptTemplate("""
            以下是检索到的参考资料（供参考）：
            ---------------------
            {context}
            ---------------------
            回答规则：
            1. 如果上述参考资料中包含直接答案，请优先依据资料准确回答；
            2. 如果参考资料中未提供答案或不完整，或者用户提出的是操作、查询、调用工具等任务型指令，
               请结合你的通用能力、上下文，并**主动检查并调用可用的工具**来完成用户的请求；
            3. 不要生硬回复“我不知道”，请尽最大努力协助用户。
            用户问题：{query}
            """);

        return RetrievalAugmentationAdvisor.builder().documentRetriever(resilientDocumentRetriever(VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .similarityThreshold(config.getSimilarityThreshold())
            .topK(config.getTopK())
            .filterExpression(filterExpression)
            .build()
        )).queryAugmenter(ContextualQueryAugmenter.builder()
            .promptTemplate(promptTemplate)
            .allowEmptyContext(Boolean.TRUE)
            .emptyContextPromptTemplate(PromptTemplate.builder().template("找不到相关的内容。").build())
            .documentFormatter(documents -> documents.stream().map(document -> {
                    String metadata = document.getMetadata().entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(", "));
                    return metadata + "\n" + document.getText();
                }).collect(Collectors.joining(System.lineSeparator()))
            ).build()
        ).build();
    }

    /**
     * 检索失败降级：向量库/embedding 等外部依赖瞬时异常（如 ES 超时、模型供应商 400/403）时，
     * 记录告警并返回空上下文继续生成，避免整条对话流被 RAG 故障打断。
     */
    public static DocumentRetriever resilientDocumentRetriever(DocumentRetriever delegate) {
        return query -> {
            try {
                return delegate.retrieve(query);
            } catch (Exception e) {
                log.warn("RAG retrieval failed, continue without retrieved context. query={}", query, e);
                return List.of();
            }
        };
    }

    /**
     * 构建租户隔离过滤条件（tenant_id == 当前租户）
     * 当前租户优先取 TenantContext，兜底 SecurityUtils
     */
    public static Filter.Expression buildTenantScopeFilter() {
        Long tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId() : SecurityUtils.getTid();
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        return builder.eq(METADATA_TENANT_ID, tenantId).build();
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

    // ------------------------------------------------------------------------------
    // Config
    // ------------------------------------------------------------------------------

    /**
     * 获取模型供应商配置
     */
    public static ModelProviderConfig resolveModelProviderConfig(@NonNull AiConfig config, @NonNull String modelProvider) {
        return MapUtils.getObject(config.getProviders(), StringUtils.nvl(modelProvider).toLowerCase(), ModelProviderConfig.builder().build());
    }

    public static ModelConfig resolveModelConfig(ModelCommonsConfig parentConfig, ModelBaseConfig modelConfig) {
        String baseUrl = StringUtils.nvl(modelConfig.getBaseUrl(), parentConfig.getBaseUrl());
        String apiKey = StringUtils.nvl(modelConfig.getApiKey(), parentConfig.getApiKey());
        String name = StringUtils.nvl(modelConfig.getName());
        return SimpleModelConfig.builder().baseUrl(baseUrl).apiKey(apiKey).name(name).build();
    }

    public static ModelConfig resolveChatModelConfig(ModelCommonsConfig parentConfig, ModelChatConfig modelConfig) {
        return resolveModelConfig(parentConfig, modelConfig);
    }

    public static ModelConfig resolveTranscriptionModelConfig(ModelCommonsConfig parentConfig, ModelTranscriptionConfig modelConfig) {
        return resolveModelConfig(parentConfig, modelConfig);
    }

    public static ModelConfig resolveSpeechModelConfig(ModelCommonsConfig parentConfig, ModelSpeechConfig modelConfig) {
        return resolveModelConfig(parentConfig, modelConfig);
    }

    public static ModelConfig resolveEmbeddingModelConfig(ModelCommonsConfig parentConfig, ModelEmbeddingConfig modelConfig) {
        return resolveModelConfig(parentConfig, modelConfig);
    }

    public static ModelConfig resolveRerankModelConfig(ModelCommonsConfig parentConfig, ModelRerankConfig modelConfig) {
        return resolveModelConfig(parentConfig, modelConfig);
    }

    public static ModelConfig resolveImageModelConfig(ModelCommonsConfig parentConfig, ModelImageConfig modelConfig) {
        return resolveModelConfig(parentConfig, modelConfig);
    }

    // ------------------------------------------------------------------------------
    // RAG
    // ------------------------------------------------------------------------------

    public static SplittingConfig resolveSplittingConfig(@NonNull SplittingConfig defaultConfig, @NonNull SplittingConfig config) {
        SplittingConfig.SplittingConfigBuilder builder = SplittingConfig.builder();
        builder.strategy(StringUtils.nvl(config.getStrategy(), defaultConfig.getStrategy()));
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

    public static VectorStoreConfig resolveVectorStoreConfig(@NonNull VectorStoreConfig defaultConfig, @NonNull VectorStoreConfig config) {
        VectorStoreConfig.VectorStoreConfigBuilder builder = VectorStoreConfig.builder();
        builder.type(nvl(config.getType(), defaultConfig.getType()));
        builder.embeddingProvider(nvl(config.getEmbeddingProvider(), defaultConfig.getEmbeddingProvider()));
        builder.indexPrefix(nvl(config.getIndexPrefix(), defaultConfig.getIndexPrefix()));
        return builder.build();
    }

    public static VectorizationConfig resolveVectorizationConfig(@NonNull VectorizationConfig defaultConfig, @NonNull VectorizationConfig config) {
        VectorizationConfig.VectorizationConfigBuilder builder = VectorizationConfig.builder();
        builder.batchSize(nvl(config.getBatchSize(), defaultConfig.getBatchSize()));
        return builder.build();
    }

    public static RagConfig resolveRagConfig(@NonNull RagConfig config) {
        RagConfig.RagConfigBuilder builder = RagConfig.builder();
        builder.store(AiUtils.resolveVectorStoreConfig(VectorStoreConfig.builder().build(), config.getStore()));
        builder.splitting(AiUtils.resolveSplittingConfig(SplittingConfig.builder().build(), config.getSplitting()));
        builder.retrieval(AiUtils.resolveRetrievalConfig(RetrievalConfig.builder().build(), config.getRetrieval()));
        builder.vectorization(AiUtils.resolveVectorizationConfig(VectorizationConfig.builder().build(), config.getVectorization()));
        return builder.build();
    }

}
