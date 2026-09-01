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
@Tag(name = "SearchLogAdminController", description = "搜索日志后台管理控制器")
public class SearchLogSysController extends AbstractController {

    private final OperationLogService operationLogService;

    @PreAuthorize("hasAnyAuthority('system:log:operation')")
    @Operation(summary = "查询搜索日志")
    @ApiResponse(description = "查询搜索日志")
    @PostMapping(API_V1_SYS_PREFIX + "/search-log/list")
    public R<Page<OperationLogEntity>> getSearchLogList(@Parameter(description = "查询搜索日志请求参数") OperationLogSearchRequest request) {
        return R.success(this.operationLogService.findOperationLogList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:log:operation')")
    @Operation(summary = "获取搜索日志详情")
    @ApiResponse(description = "获取搜索日志详情")
    @PostMapping(API_V1_SYS_PREFIX + "/search-log/detail")
    public R<OperationLogEntity> getSearchLogDetails(@Parameter(description = "日志ID") @RequestParam("日志ID") Long id) {
        return R.success(operationLogService.findById(id));
    }

}
