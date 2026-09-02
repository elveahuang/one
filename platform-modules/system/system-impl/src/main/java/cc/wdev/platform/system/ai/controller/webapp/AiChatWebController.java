package cc.wdev.platform.system.ai.controller.webapp;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.chat.SimpleChatResponse;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiAgentApi;
import cc.wdev.platform.system.ai.api.AiChatApi;
import cc.wdev.platform.system.ai.api.AiModelApi;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.request.AiAgentGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatDeleteRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.ai.domain.vo.AiChatVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelSimpleVo;
import cc.wdev.platform.system.ai.service.AiSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "AiChatController", description = "智能聊天控制器")
public class AiChatWebController extends AbstractController {

    private final AiManager aiManager;

    private final AiChatApi aiChatApi;

    private final AiModelApi aiModelApi;

    private final AiAgentApi aiAgentApi;

    private final AiSessionService aiSessionService;

    @Authenticated
    @Operation(summary = "分页查询我的对话记录")
    @ApiResponse(description = "分页查询我的对话记录")
    @GetMapping(API_V1_PREFIX + "/ai/chat/list")
    public R<Page<AiChatVo>> chatList(@Parameter(description = "查询请求") @Valid AiChatSearchRequest request) {
        request.setTenantId(SecurityUtils.getTid());
        request.setUserId(SecurityUtils.getUid());
        return R.success(aiChatApi.findMyChats(request));
    }

    @Authenticated
    @Operation(summary = "获取单个对话记录")
    @ApiResponse(description = "获取单个对话记录")
    @GetMapping(API_V1_PREFIX + "/ai/chat/details")
    public R<AiChatVo> chatDetails(@Parameter(description = "查询请求") @Valid AiChatGetRequest request) {
        if (StringUtils.isEmpty(request.getChatType())) {
            request.setChatType(AiChatType.CHAT.getValue());
        }
        return R.success(aiChatApi.getChat(request));
    }

    @Authenticated
    @OperationLog("删除对话记录")
    @Operation(summary = "删除对话记录")
    @ApiResponse(description = "删除对话记录")
    @PostMapping(API_V1_PREFIX + "/ai/chat/delete")
    public R<?> delete(@Parameter(description = "删除请求") @Valid AiChatDeleteRequest request) {
        if (request != null && request.getIds() != null) {
            request.setTenantId(SecurityUtils.getTid());
            aiChatApi.deleteChat(request);
        }
        return R.success();
    }

    @Authenticated
    @Operation(summary = "发起新对话")
    @ApiResponse(description = "发起新对话")
    @GetMapping(API_V1_PREFIX + "/ai/chat/start")
    public R<SimpleChatResponse> start(@Parameter(description = "会话ID") @RequestParam(value = "conversationId", defaultValue = "") String conversationId,
                                       @Parameter(description = "智能体编号") @RequestParam(value = "code", defaultValue = "", required = false) String code) {
        List<Message> messages = Lists.newArrayList();
        if (StringUtils.isNotEmpty(conversationId)) {
            log.debug("Start conversation with id - {}", conversationId);
            // 归属校验：仅能续接本人 + 本租户的会话
            AiSessionEntity session = this.aiSessionService.findBySessionIdAndUser(
                conversationId, SecurityUtils.getUid(), SecurityUtils.getTid());
            if (session != null) {
                messages.addAll(this.aiManager.getSessionService().getMessages(conversationId));
            }
        } else {
            conversationId = StringUtils.uuid();
            log.debug("Start new conversation with id - {}", conversationId);

            if (StringUtils.isNotEmpty(code)) {
                AiAgentVo aiAgentVo = aiAgentApi.getAiAgent(AiAgentGetRequest.builder().code(code).build());
                if (null != aiAgentVo && StringUtils.isNotBlank(aiAgentVo.getGreeting())) {
                    messages.add(AssistantMessage.builder().content(aiAgentVo.getGreeting()).build());
                }
            }
        }

        SimpleChatResponse response = SimpleChatResponse.builder()
            .conversationId(conversationId)
            .messages(messages)
            .build();
        return R.success(response);
    }

    @Authenticated
    @Operation(summary = "普通对话")
    @ApiResponse(description = "普通对话")
    @PostMapping(API_V1_PREFIX + "/ai/chat/text")
    public String chatText(@RequestBody SimpleChatRequest request) {
        return aiChatApi.chatText(request);
    }

    @Authenticated
    @Operation(summary = "流式对话")
    @ApiResponse(description = "流式对话")
    @PostMapping(value = API_V1_PREFIX + "/ai/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody SimpleChatRequest request) {
        return aiChatApi.chatStream(request);
    }

    @Authenticated
    @Operation(summary = "智能体流式对话")
    @ApiResponse(description = "智能体流式对话（ReAct 循环，输出思考/工具调用/工具结果事件与最终回答）")
    @PostMapping(value = API_V1_PREFIX + "/ai/chat/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> agentStream(@RequestBody SimpleChatRequest request) {
        return aiChatApi.chatAgentStream(request);
    }

    @Authenticated
    @Operation(summary = "模型列表")
    @ApiResponse(description = "模型列表")
    @GetMapping(API_V1_PREFIX + "/ai/chat/models")
    public R<List<AiModelSimpleVo>> aiModels() {
        return R.success(aiModelApi.getModels());
    }

    @Authenticated
    @Operation(summary = "智能体列表")
    @ApiResponse(description = "智能体列表")
    @GetMapping(API_V1_PREFIX + "/ai/chat/agents")
    public R<List<AiAgentSimpleVo>> aiAgents() {
        return R.success(aiAgentApi.getAgents());
    }

}
