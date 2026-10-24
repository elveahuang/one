package cc.wdev.platform.system.storage.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.IdsRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentSearchRequest;
import cc.wdev.platform.system.storage.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "AttachmentAdminController", description = "附件后台管理控制器")
public class AttachmentSystemController extends AbstractController {

    private final AttachmentService attachmentService;

    @PreAuthorize("hasAnyAuthority('system:attachment')")
    @Operation(summary = "获取附件列表")
    @ApiResponse(description = "获取附件列表")
    @PostMapping(API_V1_SYS_PREFIX + "/attachment/list")
    public R<Page<AttachmentEntity>> list(@Parameter(description = "附件查询请求") @Valid AttachmentSearchRequest request) {
        return R.success(attachmentService.findByPage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:attachment')")
    @Operation(summary = "获取附件详情")
    @ApiResponse(description = "获取附件详情")
    @PostMapping(API_V1_SYS_PREFIX + "/attachment/details")
    public R<AttachmentEntity> details(@Parameter(description = "附件ID") @RequestParam("id") Long id) {
        return R.success(attachmentService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:attachment')")
    @Operation(summary = "删除附件")
    @ApiResponse(description = "删除附件")
    @PostMapping(API_V1_SYS_PREFIX + "/attachment/delete")
    @OperationLog("删除附件")
    public R<?> delete(@Parameter(description = "附件ID数组") @Valid IdsRequest request) {
        attachmentService.deleteAttachments(request.getIds());
        return R.success();
    }

}
