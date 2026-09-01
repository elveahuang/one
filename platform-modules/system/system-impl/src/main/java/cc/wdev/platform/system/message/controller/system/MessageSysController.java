package cc.wdev.platform.system.message.controller.system;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.message.api.MessageApi;
import cc.wdev.platform.system.message.api.MessageChannelApi;
import cc.wdev.platform.system.message.api.MessageTypeApi;
import cc.wdev.platform.system.message.domain.entity.MessageEntity;
import cc.wdev.platform.system.message.domain.vo.MessageChannelVo;
import cc.wdev.platform.system.message.domain.vo.MessageTypeVo;
import cc.wdev.platform.system.message.request.*;
import cc.wdev.platform.system.message.service.MessageChannelService;
import cc.wdev.platform.system.message.service.MessageService;
import cc.wdev.platform.system.message.service.MessageTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "MessageSysController", description = "消息后端控制器")
public class MessageSysController extends AbstractController {

    private final MessageApi messageApi;

    private final MessageTypeApi messageTypeApi;

    private final MessageChannelApi messageChannelApi;

    private final MessageService messageService;

    private final MessageTypeService messageTypeService;

    private final MessageChannelService messageChannelService;

    // ------------------------------------------------------------------------
    // 数据初始化
    // ------------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "初始消息模版")
    @ApiResponse(description = "初始消息模版")
    @PostMapping(API_V1_SYS_PREFIX + "/message/template/init")
    @OperationLog("初始消息模版")
    public R<Void> initTemplate() {
        messageApi.initialize();
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "刷新消息模版")
    @ApiResponse(description = "刷新消息模版")
    @PostMapping(API_V1_SYS_PREFIX + "/message/template/sync")
    @OperationLog("刷新消息模版")
    public R<Void> syncTemplate() {
        messageApi.initializeMessageTemplate(true);
        return R.success();
    }

    // ------------------------------------------------------------------------
    // 消息通道
    // ------------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "获取消息通道列表")
    @ApiResponse(description = "获取消息通道列表")
    @PostMapping(API_V1_SYS_PREFIX + "/message/channel/list")
    public R<Page<MessageChannelVo>> messageChannelList(@Valid PageRequest request) {
        return R.success(this.messageChannelApi.findMessageChannel(request));
    }

    @Authenticated
    @Operation(summary = "获取消息推送类型")
    @ApiResponse(description = "获取消息推送类型")
    @PostMapping(API_V1_SYS_PREFIX + "/message/channel/search")
    public R<?> search(@Valid MessageChannelSearchRequest request) {
        request.setStatus(StatusTypeEnum.ON.getValue());
        return R.success(messageChannelApi.search(request));
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "获取消息通道详情")
    @ApiResponse(description = "获取消息通道详情")
    @PostMapping(API_V1_SYS_PREFIX + "/message/channel/details")
    public R<MessageChannelVo> messageChannelDetails(@Parameter(description = "消息通道ID") @RequestParam("id") Long id) {
        return R.success(this.messageChannelApi.getMessageChannel(MessageChannelRequest.builder().id(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "保存消息通道")
    @ApiResponse(description = "保存消息通道")
    @OperationLog("保存消息通道")
    @PostMapping(API_V1_SYS_PREFIX + "/message/channel/save")
    public R<Void> messageChannelSave(@RequestBody @Valid MessageChannelSaveRequest request) {
        this.messageChannelApi.saveMessageChannel(request);
        return R.success();
    }

    // ------------------------------------------------------------------------
    // 消息类型和模版管理
    // ------------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "获取消息类型列表")
    @ApiResponse(description = "获取消息类型列表")
    @PostMapping(API_V1_SYS_PREFIX + "/message/type/list")
    public R<Page<MessageTypeVo>> messageTypeList(@Valid PageRequest request) {
        return R.success(this.messageTypeApi.findMessageType(request));
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "获取消息类型和模版详情")
    @ApiResponse(description = "获取消息类型和模版详情")
    @PostMapping(API_V1_SYS_PREFIX + "/message/type/details")
    public R<MessageTypeVo> messageTypeDetails(@Parameter(description = "消息类型ID") @RequestParam("id") Long id) {
        return R.success(this.messageTypeApi.getMessageType(MessageTypeRequest.builder().id(id).withItem(true).build()));
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "保存消息类型")
    @ApiResponse(description = "保存消息类型")
    @OperationLog("保存消息类型")
    @PostMapping(API_V1_SYS_PREFIX + "/message/type/save")
    public R<Void> messageTypeSave(@RequestBody @Valid MessageTypeSaveRequest request) {
        this.messageTypeApi.saveMessageType(request);
        return R.success();
    }

    // ------------------------------------------------------------------------
    // 消息列表
    // ------------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "获取消息列表")
    @ApiResponse(description = "获取消息列表")
    @PostMapping(API_V1_SYS_PREFIX + "/message/list")
    public R<Page<MessageEntity>> messageList(@Valid PageRequest request) {
        return R.success(messageService.findMessageList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "获取消息详情")
    @ApiResponse(description = "获取消息详情")
    @PostMapping(API_V1_SYS_PREFIX + "/message/details")
    public R<MessageEntity> messageDetails(@Parameter(description = "消息ID") @RequestParam("id") Long id) {
        return R.success(messageService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:message')")
    @Operation(summary = "发送消息")
    @ApiResponse(description = "发送消息")
    @OperationLog("发送消息")
    @PostMapping(API_V1_SYS_PREFIX + "/message/send")
    public R<Void> messageSend(@Parameter(description = "消息ID") @RequestParam("id") Long id) throws Exception {
        messageApi.sendMessage(id);
        return R.success();
    }

}
