package cc.wdev.platform.system.im.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.im.api.ChatEntitySessionApi;
import cc.wdev.platform.system.im.api.ImChatApi;
import cc.wdev.platform.system.im.domain.request.*;
import cc.wdev.platform.system.im.domain.vo.ChatEntitySessionVo;
import cc.wdev.platform.system.im.domain.vo.ChatMessageVo;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_WEB_PREFIX;

/**
 * 即时通讯（IM）控制器。
 *
 * <p>所有接口的身份一律取自 {@link SecurityUtils}，入参中的 userId 会被覆盖，避免越权读写他人会话。
 * 发送消息后由 {@code ImChatApiImpl} 在事务提交后经 WebSocket 点对点推送，离线场景靠历史消息接口兜底。</p>
 *
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "ImChatWebController", description = "即时通讯控制器")
public class ImChatWebController extends AbstractController {

    private final ImChatApi imChatApi;

    private final ChatEntitySessionApi chatEntitySessionApi;

    @Authenticated
    @Operation(summary = "我的会话列表")
    @ApiResponse(description = "我的会话列表")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/list")
    public R<List<ChatSessionVo>> sessions(@Parameter(description = "查询请求") @RequestBody @Valid ChatSearchRequest request) {
        // 强制限定为当前用户，忽略入参中的 userIds
        request.setUserIds(List.of(SecurityUtils.getUid()));
        return R.success(imChatApi.list(request));
    }

    @Authenticated
    @OperationLog("创建或获取即时会话")
    @Operation(summary = "创建或获取即时会话")
    @ApiResponse(description = "创建或获取即时会话，已存在同一对用户的会话时直接复用")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/save")
    public R<ChatSessionVo> saveSession(@Parameter(description = "会话请求") @RequestBody @Valid ChatRequest request) {
        // 发起人由服务端覆盖为当前登录用户
        request.setUserId(SecurityUtils.getUid());
        request.setEntitySessionUserId(SecurityUtils.getUid());
        return R.success(imChatApi.saveChatSession(request));
    }

    @Authenticated
    @Operation(summary = "会话历史消息")
    @ApiResponse(description = "会话历史消息")
    @PostMapping(API_V1_WEB_PREFIX + "/im/message/list")
    public R<Page<ChatMessageVo>> messages(@Parameter(description = "查询请求") @RequestBody @Valid ChatMessageRequest request) {
        request.setUserId(SecurityUtils.getUid());
        return R.success(imChatApi.findChatMessages(request));
    }

    @Authenticated
    @Operation(summary = "会话未读消息数")
    @ApiResponse(description = "会话未读消息数")
    @PostMapping(API_V1_WEB_PREFIX + "/im/message/unread")
    public R<Long> unread(@Parameter(description = "查询请求") @RequestBody @Valid ChatSessionMessageCountRequest request) {
        request.setUserId(SecurityUtils.getUid());
        return R.success(imChatApi.getChatSessionMessageCount(request));
    }

    @Authenticated
    @OperationLog("发送即时消息")
    @Operation(summary = "发送即时消息")
    @ApiResponse(description = "发送即时消息")
    @PostMapping(API_V1_WEB_PREFIX + "/im/message/send")
    public R<ChatMessageVo> send(@Parameter(description = "消息内容") @RequestBody @Valid ChatMessageSaveRequest request) {
        return R.success(imChatApi.saveChatMessage(request));
    }

    @Authenticated
    @OperationLog("标记即时消息已读")
    @Operation(summary = "标记即时消息已读")
    @ApiResponse(description = "标记即时消息已读")
    @PostMapping(API_V1_WEB_PREFIX + "/im/message/read")
    public R<?> read(@Parameter(description = "已读请求") @RequestBody @Valid ChatEntityMessageRequest request) {
        request.setUserId(SecurityUtils.getUid());
        imChatApi.saveChatEntityMessage(request);
        return R.success();
    }

    @Authenticated
    @OperationLog("会话置顶")
    @Operation(summary = "会话置顶")
    @ApiResponse(description = "会话置顶")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/top")
    public R<?> top(@Parameter(description = "会话ID") @RequestParam("chatSessionId") Long chatSessionId) {
        imChatApi.doTop(chatSessionId);
        return R.success();
    }

    @Authenticated
    @OperationLog("取消会话置顶")
    @Operation(summary = "取消会话置顶")
    @ApiResponse(description = "取消会话置顶")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/untop")
    public R<?> untop(@Parameter(description = "会话ID") @RequestParam("chatSessionId") Long chatSessionId) {
        imChatApi.undoTop(chatSessionId);
        return R.success();
    }

    @Authenticated
    @OperationLog("收藏会话")
    @Operation(summary = "收藏会话")
    @ApiResponse(description = "收藏会话")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/collect")
    public R<ChatEntitySessionVo> collect(@Parameter(description = "会话ID") @RequestParam("chatSessionId") Long chatSessionId) {
        return R.success(imChatApi.doCollect(chatSessionId));
    }

    @Authenticated
    @OperationLog("取消收藏会话")
    @Operation(summary = "取消收藏会话")
    @ApiResponse(description = "取消收藏会话")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/uncollect")
    public R<ChatEntitySessionVo> uncollect(@Parameter(description = "会话ID") @RequestParam("chatSessionId") Long chatSessionId) {
        return R.success(imChatApi.undoCollect(chatSessionId));
    }

    @Authenticated
    @OperationLog("清空会话")
    @Operation(summary = "清空会话")
    @ApiResponse(description = "清空会话")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/clear")
    public R<?> clear(@Parameter(description = "会话ID") @RequestParam("chatSessionId") Long chatSessionId) {
        imChatApi.clear(chatSessionId);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "会话读取态")
    @ApiResponse(description = "会话读取态")
    @PostMapping(API_V1_WEB_PREFIX + "/im/session/details")
    public R<ChatEntitySessionVo> sessionDetails(@Parameter(description = "会话ID") @RequestParam("chatSessionId") Long chatSessionId) {
        return R.success(chatEntitySessionApi.getChatEntitySession(chatSessionId));
    }

}
