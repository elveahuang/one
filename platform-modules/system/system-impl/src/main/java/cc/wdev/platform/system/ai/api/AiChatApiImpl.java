package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.ai.enums.AiResponseType;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.NumberUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.request.*;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.ai.domain.vo.AiChatVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import cc.wdev.platform.system.ai.service.AiChatMemoryService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.agent.advisors.AutoMemoryToolsAdvisor;
import org.springaicommunity.agent.dream.AutoDreamAdvisor;
import org.springaicommunity.agent.dream.AutoDreamService;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;
import java.util.List;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.AI_INVALID_CHAT_TYPE;
import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * 智能聊天服务实现
 *
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatApiImpl implements AiChatApi {

    private static final int TITLE_MAX_LENGTH = 30;

    private final AiManager aiManager;

    private final AiHelper aiHelper;

    private final AiModelApi aiModelApi;

    private final AiAgentApi aiAgentApi;

    private final AiKbApi aiKbApi;

    private final SessionService sessionService;

    private final AiSessionService aiSessionService;

    private final AiChatMemoryService aiChatMemoryService;

    /**
     * @see AiChatApi#chatText(SimpleChatRequest)
     */
    @Override
    public String chatText(SimpleChatRequest request) {
        preHandleChatRequest(request);

        log.info("chatText [{}] start", request.getConversationId());
        ChatClient chatClient = this.getChatClient(request);
        log.info("chatText [{}] process", request.getConversationId());
        ChatClient.ChatClientRequestSpec spec = AiUtils.processChatSpec(chatClient, request);
        return spec.call().content();
    }

    /**
     * @see AiChatApi#chatStream(SimpleChatRequest)
     */
    @Override
    public Flux<String> chatStream(SimpleChatRequest request) {
        preHandleChatRequest(request);

        log.info("chatStream [{}] start", request.getConversationId());
        ChatClient chatClient = this.getChatClient(request);
        log.info("chatStream [{}] process", request.getConversationId());
        ChatClient.ChatClientRequestSpec spec = AiUtils.processChatSpec(chatClient, request);
        if (StringUtils.isNotEmpty(request.getResponseType()) && AiResponseType.JSON.getValue().equalsIgnoreCase(request.getResponseType())) {
            try {
                log.info("handleStreamChat [{}] json", request.getConversationId());
                Flux<String> flux = spec.stream().content().map(AiUtils::getTextContent);
                return Flux.concat(Mono.just(AiUtils.getStartContent()), flux, Mono.just(AiUtils.getEndContent()));
            } catch (Exception e) {
                log.error("handleStreamChat [{}] error", request.getConversationId(), e);
                return Flux.just(AiUtils.getErrorContent());
            }
        } else {
            log.info("handleStreamChat [{}] text", request.getConversationId());
            return spec.stream().content();
        }
    }

    /**
     * @see AiChatApi#getChat(AiChatGetRequest)
     */
    @Override
    public AiChatVo getChat(@NonNull AiChatGetRequest request) {
        if (StringUtils.isEmpty(request.getConversationId())) {
            return null;
        }
        AiSessionEntity session = this.aiSessionService.findBySessionIdAndUser(request.getConversationId(), SecurityUtils.getUid(), SecurityUtils.getTid());
        if (session == null) {
            return null;
        }
        List<Message> messages = this.sessionService.getMessages(session.getSessionId());
        return this.toChatVo(session, request.getChatType(), messages);
    }

    /**
     * @see AiChatApi#deleteChat(AiChatDeleteRequest)
     */
    @Override
    public boolean deleteChat(AiChatDeleteRequest request) {
        if (request == null || request.getIds() == null || request.getIds().length == 0) {
            return false;
        }
        Long userId = SecurityUtils.getUid();
        Long tenantId = SecurityUtils.getTid();
        for (String sessionId : request.getIds()) {
            AiSessionEntity session = this.aiSessionService.findBySessionIdAndUser(sessionId, userId, tenantId);
            if (StringUtils.isNotEmpty(sessionId) && session != null) {
                this.sessionService.delete(sessionId);
                this.aiChatMemoryService.deleteByConversationId(sessionId);
            }
        }
        return true;
    }

    /**
     * @see AiChatApi#findMyChats(AiChatSearchRequest)
     */
    @Override
    public Page<AiChatVo> findMyChats(AiChatSearchRequest request) {
        String userId = String.valueOf(SecurityUtils.getUid());
        Long tenantId = SecurityUtils.getTid();
        return this.aiSessionService.findByUserIdPage(userId, tenantId, request.getPageable())
            .map(session -> this.toChatVo(session, request.getChatType(),
                this.sessionService.getMessages(session.getSessionId())));
    }

    // ------------------------------------------------------------------------
    // 私有辅助方法
    // ------------------------------------------------------------------------

    /**
     * 预处理请求
     * 1. 重要参数，比如租户和用户信息等，不管前端有没有传参数过来都直接覆盖
     * 2. 其他参数，前端没传参数过来，那么按预设的复制
     */
    private void preHandleChatRequest(SimpleChatRequest request) {
        request.setTenantId(SecurityUtils.getTid());
        request.setUserId(null != request.getUserId() && request.getUserId() > 0 ? request.getUserId() : SecurityUtils.getUid());
        request.setConversationId(nvl(request.getConversationId(), AiUtils.generateConversationId()));
        request.setResponseType(nvl(request.getResponseType(), AiResponseType.TEXT.getValue()));
    }

    /**
     * 获取ChatClient
     */
    private ChatClient getChatClient(SimpleChatRequest request) {
        return switch (BaseEnum.getEnumByValue(request.getChatType(), AiChatType.class, AiChatType.NONE)) {
            case AiChatType.STATIC -> this.getDefaultChatClient(request);
            case AiChatType.CHAT -> this.getChatClientByModel(request);
            case AiChatType.AGENT -> this.getChatClientByAgent(request);
            case AiChatType.KB -> this.getChatClientByKb(request);
            case AiChatType.NONE -> throw new ServiceException(AI_INVALID_CHAT_TYPE);
        };
    }

    /**
     * 获取系统内置对话模型ChatClient
     */
    private ChatClient getDefaultChatClient(SimpleChatRequest request) {
        ChatModel model = this.aiManager.getChatModel();

        ChatClient.Builder builder = ChatClient.builder(model);
        if (request.getWithSkills()) {
            this.applySkillsTool(builder);
        }
        if (request.getWithSession()) {
            this.applyAdvisors(builder);
        }
        if (request.getWithRag()) {
            this.applyRagAdvisors(builder);
        }
        if (request.getWithMemory()) {
            this.applyMemoryAdvisor(builder);
        }
        return builder.build();
    }

    /**
     * 获取模型对话的ChatClient
     */
    private ChatClient getChatClientByModel(SimpleChatRequest request) {
        AiModelVo modelVo = this.aiModelApi.getAiModel(AiModelGetRequest.builder()
            .id(request.getModelId())
            .code(request.getModelCode())
            .build()
        );

        ChatModel model = this.aiManager.getChatModel(SimpleModelConfig.builder()
            .name(modelVo.getModelName())
            .modelType(modelVo.getModelType())
            .serviceProvider(modelVo.getServiceProvider())
            .modelProvider(modelVo.getModelProvider())
            .baseUrl(modelVo.getBaseUrl())
            .apiKey(modelVo.getApiKey())
            .build());

        return ChatClient.builder(model).build();
    }

    /**
     * 获取智能体对话的ChatClient
     */
    private ChatClient getChatClientByAgent(SimpleChatRequest request) {
        AiAgentVo agent = this.aiAgentApi.getAiAgent(AiAgentGetRequest.builder()
            .id(request.getAgentId())
            .code(request.getAgentCode())
            .build()
        );

        ChatModel model = this.aiManager.getChatModel(SimpleModelConfig.builder()
            .name(agent.getModel().getModelName())
            .modelType(agent.getModel().getModelType())
            .serviceProvider(agent.getModel().getServiceProvider())
            .modelProvider(agent.getModel().getModelProvider())
            .baseUrl(agent.getModel().getBaseUrl())
            .apiKey(agent.getModel().getApiKey())
            .build());

        ChatClient.Builder builder = ChatClient.builder(model);
        this.applyTools(builder, agent);
        this.applyAdvisors(builder);
        this.applyRagAdvisors(builder, agent);
        // 智能体系统提示词（模板渲染）与温度
        if (StringUtils.isNotEmpty(agent.getSystemPrompt())) {
            builder.defaultSystem(AiUtils.renderPrompt(agent.getSystemPrompt(), request.getParams()));
        }
        if (agent.getTemperature() != null && agent.getTemperature().doubleValue() > 0) {
            builder.defaultOptions(ChatOptions.builder().temperature(agent.getTemperature().doubleValue()));
        }
        return builder.build();
    }

    /**
     * 获取知识库对话的ChatClient
     */
    private ChatClient getChatClientByKb(SimpleChatRequest request) {
        AiKbVo kbVo = this.aiKbApi.getKb(GetRequest.builder()
            .id(request.getKbId())
            .code(request.getKbCode())
            .build()
        );

        ChatModel chatModel = this.aiManager.getChatModel(SimpleModelConfig.builder()
            .name(kbVo.getChatModel().getModelName())
            .modelType(kbVo.getChatModel().getModelType())
            .serviceProvider(kbVo.getChatModel().getServiceProvider())
            .modelProvider(kbVo.getChatModel().getModelProvider())
            .baseUrl(kbVo.getChatModel().getBaseUrl())
            .apiKey(kbVo.getChatModel().getApiKey())
            .build());

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        this.applyAdvisors(builder);
        this.applyRagAdvisors(builder, kbVo);
        if (request.getTemperature() != null && request.getTemperature() > 0) {
            builder.defaultOptions(ChatOptions.builder().temperature(request.getTemperature().doubleValue()));
        }
        return builder.build();
    }

    /**
     * 对话增加会话存储和日志的支持
     */
    private void applyTools(ChatClient.Builder builder, final List<String> toolNames) {
        if (CollectionUtils.isNotEmpty(toolNames)) {
            List<ToolCallback> objects = Lists.newArrayList();
            this.aiManager.getToolCallbackResolver().ifAvailable(resolver -> {
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
     * 对话增加工具支持
     */
    private void applySkillsTool(ChatClient.Builder builder) {
        if (this.aiManager.getConfig().getSkill().isEnabled()
            && CollectionUtils.isNotEmpty(this.aiManager.getConfig().getSkill().getPaths())) {

            SkillsTool.Builder stb = SkillsTool.builder();
            for (Resource resource : this.aiManager.getConfig().getSkill().getPaths()) {
                if (resource.exists()) {
                    log.info("Add skills directory: {}", resource.getDescription());
                    stb.addSkillsResource(resource);
                } else {
                    log.info("Skills directory {} not exists", resource.getDescription());
                }
            }

            builder.defaultTools(stb.build());
        }
    }

    /**
     * 智能体对话增加工具支持
     */
    private void applyTools(ChatClient.Builder builder, AiAgentVo agent) {
        if (this.aiManager.getConfig().getSkill().isEnabled() && CollectionUtils.isNotEmpty(this.aiManager.getConfig().getSkill().getPaths())) {
            SkillsTool.Builder stb = SkillsTool.builder();
            this.aiManager.getConfig().getSkill().getPaths().forEach(stb::addSkillsResource);
            builder.defaultTools(stb.build());
        }
        this.applyTools(builder, agent.getToolNames());
    }

    /**
     * 对话增加会话存储和日志的支持
     */
    private void applyAdvisors(ChatClient.Builder builder) {
        // 对话存储
        builder.defaultAdvisors(AiUtils.getCustomLoggingAdvisor());
        builder.defaultAdvisors(AiUtils.getCustomContextAdvisor());
        builder.defaultAdvisors(this.aiManager.getSessionMetadataAdvisor());
        builder.defaultAdvisors(this.aiManager.getSessionMemoryAdvisor());
    }

    /**
     * 静态对话增加知识检索支持
     */
    private void applyRagAdvisors(ChatClient.Builder builder) {
        builder.defaultAdvisors(this.aiManager.getRetrievalAugmentationAdvisor());
    }

    /**
     * 智能体对话增加知识检索支持
     */
    private void applyRagAdvisors(ChatClient.Builder builder, @NonNull AiAgentVo agent) {
        AiKbVo kbVo = this.aiKbApi.getKb(GetRequest.builder().id(agent.getKbId()).build());
        applyRagAdvisors(builder, kbVo);
    }

    /**
     * 知识库对话增加知识检索支持
     */
    private void applyRagAdvisors(ChatClient.Builder builder, @NonNull AiKbVo kb) {
        RetrievalAugmentationAdvisor advisor = this.aiHelper.resolveRetrievalAugmentationAdvisor(kb);
        if (advisor != null) {
            builder.defaultAdvisors(advisor);
        }
    }

    /**
     * 对话增加长期记忆支持
     */
    private void applyMemoryAdvisor(ChatClient.Builder builder) {
        if (this.aiManager.getConfig().getMemory().isEnabled() && StringUtils.isNotEmpty(this.aiManager.getConfig().getMemory().getPath())) {
            // 长期记忆需要按租户和用户进行隔离
            String path = Paths.get(this.aiManager.getConfig().getMemory().getPath())
                .resolve(String.valueOf(SecurityUtils.getTid()))
                .resolve(String.valueOf(SecurityUtils.getUid()))
                .normalize()
                .toString();
            log.info("Apply memory directory: {}", path);

            // AutoMemoryToolsAdvisor
            AutoMemoryToolsAdvisor autoMemoryToolsAdvisor = AutoMemoryToolsAdvisor.builder()
                .memoriesRootDirectory(path)
                .build();
            builder.defaultAdvisors(autoMemoryToolsAdvisor);

            // AutoDreamAdvisor
            AutoDreamService autoDreamService = AutoDreamService.builder(builder.clone()).build();
            AutoDreamAdvisor autoDreamAdvisor = AutoDreamAdvisor.builder()
                .memoriesRootDirectory(path)
                .dreamService(autoDreamService)
                .build();
            builder.defaultAdvisors(autoDreamAdvisor);
        }
    }

    // ------------------------------------------------------------------------
    // Chat History
    // ------------------------------------------------------------------------

    private AiChatVo toChatVo(AiSessionEntity session, String chatType, List<Message> messages) {
        Long userId = NumberUtils.toLong(session.getUserId());
        return AiChatVo.builder()
            .id(session.getId())
            .tenantId(session.getTenantId())
            .userId(userId != null ? userId : 0L)
            .type(chatType)
            .conversationId(session.getSessionId())
            .title(this.resolveTitle(session, messages))
            .messages(messages)
            .createdAt(session.getCreatedAt())
            .build();
    }

    private String resolveTitle(AiSessionEntity session, List<Message> messages) {
        if (CollectionUtils.isNotEmpty(messages)) {
            for (Message message : messages) {
                if (message instanceof UserMessage userMessage && StringUtils.isNotEmpty(userMessage.getText())) {
                    String text = userMessage.getText().trim();
                    return text.length() > TITLE_MAX_LENGTH ? text.substring(0, TITLE_MAX_LENGTH) + "..." : text;
                }
            }
        }
        return session.getSessionId();
    }

}
