package cc.wdev.platform.system.log.controller.system;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.log.domain.entity.OperationLogEntity;
import cc.wdev.platform.system.log.domain.request.OperationLogSearchRequest;
import cc.wdev.platform.system.log.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author Irving
 */
@AllArgsConstructor
@RestController
@Tag(name = "OperationLogAdminController", description = "操作日志后台管理控制器")
public class OperationLogSysController extends AbstractController {

    private final OperationLogService operationLogService;

    @PreAuthorize("hasAnyAuthority('system:log:operation')")
    @PermitAll
    @Operation(summary = "获取操作日志")
    @ApiResponse(description = "获取操作日志")
    @PostMapping(API_V1_SYS_PREFIX + "/operation-log/list")
    public R<Page<OperationLogEntity>> getOperationLogList(OperationLogSearchRequest request) {
        return R.success(this.operationLogService.findOperationLogList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:log:operation')")
    @Operation(summary = "获取操作日志详情")
    @ApiResponse(description = "获取操作日志详情")
    @PostMapping(API_V1_SYS_PREFIX + "/operation-log/detail")
    public R<OperationLogEntity> details(@Parameter(description = "操作日志ID") @RequestParam("id") Long id) {
        return R.success(operationLogService.findById(id));
    }

}
