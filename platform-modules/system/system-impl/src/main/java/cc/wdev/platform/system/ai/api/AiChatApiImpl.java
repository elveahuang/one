package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.domain.GetRequest;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.NumberUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiChatDeleteRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.*;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.*;

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

    private final AiMcpApi aiMcpApi;

    private final SessionService sessionService;

    private final AiSessionService aiSessionService;

    private final AiSessionEventService aiSessionEventService;

    /**
     * @see AiChatApi#chatText(SimpleChatRequest)
     */
    @Override
    public String chatText(SimpleChatRequest request) {
        AiUtils.processChatRequest(request);
        log.info("chatText [{}] start", request.getConversationId());
        ChatClient chatClient = this.getChatClient(request);
        log.info("chatText [{}] process", request.getConversationId());
        ChatClient.ChatClientRequestSpec spec = AiUtils.processChatSpec(chatClient, request);
        log.info("chatText [{}] response", request.getConversationId());
        return AiUtils.processChatResponse(spec, request);
    }

    /**
     * @see AiChatApi#chatStream(SimpleChatRequest)
     */
    @Override
    public Flux<String> chatStream(SimpleChatRequest request) {
        AiUtils.processChatRequest(request);
        log.info("chatStream [{}] start", request.getConversationId());
        ChatClient chatClient = this.getChatClient(request);
        log.info("chatStream [{}] process", request.getConversationId());
        ChatClient.ChatClientRequestSpec spec = AiUtils.processChatSpec(chatClient, request);
        log.info("chatStream [{}] response", request.getConversationId());
        return AiUtils.processStreamChatResponse(spec, request);
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
        Page<AiChatVo> page = this.aiSessionService.findPageByUserId(userId, tenantId, request.getPageable())
            .map(session -> this.toChatVo(session, request.getChatType(), List.of()));
        // 批量查询会话首条用户消息生成标题，避免逐会话加载全量消息的 N+1
        List<String> sessionIds = page.getContent().stream().map(AiChatVo::getConversationId).toList();
        Map<String, String> titles = new HashMap<>();
        for (AiSessionEventEntity event : this.aiSessionEventService.findUserMessagesBySessionIds(sessionIds)) {
            titles.putIfAbsent(event.getSessionId(), StringUtils.nvl(event.getMessageContent()));
        }
        page.getContent().forEach(vo -> {
            String title = titles.get(vo.getConversationId());
            if (StringUtils.isNotEmpty(title)) {
                vo.setTitle(title.length() > TITLE_MAX_LENGTH ? title.substring(0, TITLE_MAX_LENGTH) + "..." : title);
            }
        });
        return page;
    }

    // ------------------------------------------------------------------------
    // 私有辅助方法
    // ------------------------------------------------------------------------

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
        ChatClient.Builder builder = ChatClient.builder(this.aiManager.getChatModel());
        if (request.getWithAgentEnabled()) {
            this.aiManager.applyAgentTool(builder);
        }
        if (request.getWithSession()) {
            this.aiManager.applyBaseAdvisors(builder);
        }
        if (request.getWithRag()) {
            this.aiManager.applyRagAdvisors(builder);
        }
        if (request.getWithMemory()) {
            this.aiManager.applyMemoryAdvisor(builder);
        }
        if (CollectionUtils.isNotEmpty(request.getToolNames())) {
            this.aiManager.applyTools(builder, request.getToolNames());
        }
        return builder.build();
    }

    /**
     * 获取模型对话的ChatClient
     */
    private ChatClient getChatClientByModel(SimpleChatRequest request) {
        AiModelVo modelVo = this.aiModelApi.getAiModel(GetRequest.of(request.getModelId(), request.getModelCode()));

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
        AiAgentVo agent = this.aiAgentApi.getAiAgent(GetRequest.of(request.getAgentId(), request.getAgentCode()));
        if (agent.getModel() == null) {
            throw new ServiceException(AI_INVALID_AGENT_MODEL);
        }

        ChatModel model = this.aiManager.getChatModel(SimpleModelConfig.builder()
            .name(agent.getModel().getModelName())
            .modelType(agent.getModel().getModelType())
            .serviceProvider(agent.getModel().getServiceProvider())
            .modelProvider(agent.getModel().getModelProvider())
            .baseUrl(agent.getModel().getBaseUrl())
            .apiKey(agent.getModel().getApiKey())
            .build());

        ChatClient.Builder builder = ChatClient.builder(model);
        this.aiManager.applyAgentTool(builder);
        this.aiManager.applyTools(builder, agent.getToolNames());
        // 挂载智能体绑定的外部 MCP 工具（此前 mcpServerIds 只存不用）
        this.applyMcpTools(builder, agent.getMcpServerIds());
        this.aiManager.applyBaseAdvisors(builder);
        this.aiManager.applyMemoryAdvisor(builder);
        // 智能体绑定了知识库才挂载 RAG；未绑定时 getKb 会因 id/code 双空抛 PARAM_ERROR
        if (agent.getKbId() != null) {
            this.aiHelper.applyRagAdvisors(builder, this.aiKbApi.getKb(GetRequest.builder().id(agent.getKbId()).build()));
        }
        return builder.build();
    }

    /**
     * 把智能体绑定的外部 MCP Server 工具挂进 ChatClient。
     *
     * <p>Spring AI 的默认 {@code ToolCallbackResolver} 会显式过滤 MCP 提供者，因此外部 MCP 工具
     * 必须通过 {@code defaultTools(provider)} 挂载，不能登记成 toolNames 走按名解析。
     * 单个 MCP Server 不可用时只跳过该 Server，不影响对话主流程。</p>
     */
    private void applyMcpTools(ChatClient.Builder builder, List<Long> mcpServerIds) {
        if (CollectionUtils.isEmpty(mcpServerIds)) {
            return;
        }
        try {
            ToolCallbackProvider provider = this.aiMcpApi.buildToolCallbackProvider(mcpServerIds);
            if (provider != null) {
                builder.defaultTools(provider);
                log.info("Agent MCP tools attached, servers - [{}].", mcpServerIds.size());
            }
        } catch (Exception e) {
            // MCP 故障不阻断对话
            log.warn("Attach agent MCP tools failed.", e);
        }
    }

    /**
     * 获取知识库对话的ChatClient
     */
    private ChatClient getChatClientByKb(SimpleChatRequest request) {
        AiKbVo kb = this.aiKbApi.getKb(GetRequest.of(request.getKbId(), request.getKbCode()));
        if (kb.getChatModel() == null) {
            throw new ServiceException(AI_INVALID_KB_MODEL);
        }

        ChatModel chatModel = this.aiManager.getChatModel(SimpleModelConfig.builder()
            .name(kb.getChatModel().getModelName())
            .modelType(kb.getChatModel().getModelType())
            .serviceProvider(kb.getChatModel().getServiceProvider())
            .modelProvider(kb.getChatModel().getModelProvider())
            .baseUrl(kb.getChatModel().getBaseUrl())
            .apiKey(kb.getChatModel().getApiKey())
            .build());

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        this.aiManager.applyBaseAdvisors(builder);
        this.aiHelper.applyRagAdvisors(builder, kb);
        return builder.build();
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
