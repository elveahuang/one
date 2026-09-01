package cc.wdev.platform.system.message.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "NoticeSysController", description = "系统通知后端控制器")
public class NoticeSysController extends AbstractController {

    private final NoticeService noticeService;

    @Operation(summary = "获取系统消通知列表")
    @ApiResponse(description = "获取系统消通知列表")
    @PostMapping(API_V1_SYS_PREFIX + "/notice/list")
    public R<Page<NoticeEntity>> list(NoticeSearchRequest noticeSearchRequest) {
        return R.success(noticeService.findNoticeByPage(noticeSearchRequest));
    }

    @Operation(summary = "获取系统消通知详情")
    @ApiResponse(description = "获取通知详情并修改通知状态")
    @PostMapping(API_V1_SYS_PREFIX + "/notice/details")
    public R<NoticeEntity> details(@Parameter(description = "通知ID") @RequestParam("id") Long id) {
        NoticeEntity notice = new NoticeEntity();
        notice.setId(id);
        notice.setReadInd(true);
        noticeService.updateById(notice);
        return R.success(noticeService.findById(id));
    }

    @Operation(summary = "删除公告资讯")
    @ApiResponse(description = "删除公告资讯")
    @PostMapping(API_V1_SYS_PREFIX + "/notice/delete")
    @OperationLog("删除公告资讯")
    public R<Void> delete(DeleteRequest request) {
        if (request != null && request.getIds() != null && request.getIds().length > 0) {
            noticeService.softDeleteBatchById(Arrays.asList(request.getIds()));
        }
        return R.success();
    }

}
