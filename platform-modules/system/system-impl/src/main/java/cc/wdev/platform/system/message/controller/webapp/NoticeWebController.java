package cc.wdev.platform.system.message.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.message.domain.entity.NoticeEntity;
import cc.wdev.platform.system.message.request.NoticeSearchRequest;
import cc.wdev.platform.system.message.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "NoticeController", description = "系统通知控制器")
public class NoticeWebController extends AbstractController {

    private final NoticeService noticeService;

    @Authenticated
    @Operation(summary = "获取系统通知列表")
    @ApiResponse(description = "获取系统通知列表")
    @PostMapping(API_V1_PREFIX + "/notice/list")
    public R<Page<NoticeEntity>> list(NoticeSearchRequest request) {
        return R.success(noticeService.findMyNoticeByUserId(request));
    }

    @Authenticated
    @Operation(summary = "获取系统通知详情")
    @ApiResponse(description = "获取系统通知详情")
    @PostMapping(API_V1_PREFIX + "/notice/details")
    public R<NoticeEntity> details(@Parameter(description = "通知ID") @RequestParam Long id) {
        NoticeEntity entity = noticeService.findDetailsForRecipient(id);
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.OPERATION_PERMISSION_ERROR);
        }
        return R.success(entity);
    }

    @Authenticated
    @Operation(summary = "获取未读通知数")
    @ApiResponse(description = "获取未读通知数")
    @PostMapping(API_V1_PREFIX + "/notice/unread-count")
    public R<Long> unreadCount() {
        return R.success(noticeService.countUnread());
    }

    @Authenticated
    @OperationLog("标记通知已读")
    @Operation(summary = "标记通知已读")
    @ApiResponse(description = "标记通知已读")
    @PostMapping(API_V1_PREFIX + "/notice/read")
    public R<?> read(@Parameter(description = "通知ID列表") @RequestBody(required = false) List<Long> ids) {
        noticeService.markRead(ids);
        return R.success();
    }

    @Authenticated
    @OperationLog("全部通知标记已读")
    @Operation(summary = "全部通知标记已读")
    @ApiResponse(description = "全部通知标记已读")
    @PostMapping(API_V1_PREFIX + "/notice/read-all")
    public R<?> readAll() {
        noticeService.markAllRead();
        return R.success();
    }

}
