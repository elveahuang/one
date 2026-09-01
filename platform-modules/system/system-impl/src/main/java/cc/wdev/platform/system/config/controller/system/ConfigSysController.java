package cc.wdev.platform.system.config.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import cc.wdev.platform.system.config.domain.request.ConfigSaveRequest;
import cc.wdev.platform.system.config.domain.request.ConfigSearchRequest;
import cc.wdev.platform.system.config.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author irving
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "ConfigAdminController", description = "系统设置项管理控制器")
public class ConfigSysController extends AbstractController {

    private final ConfigApi configApi;

    private final ConfigService configService;

    @PreAuthorize("hasAnyAuthority('system:config')")
    @OperationLog("初始化系统参数")
    @Operation(summary = "初始化系统参数")
    @ApiResponse(description = "初始化系统参数")
    @PostMapping(API_V1_SYS_PREFIX + "/config/initialize")
    public R<Void> initialize() {
        configApi.initialize();
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:config')")
    @Operation(summary = "获取系统设置项列表")
    @ApiResponse(description = "获取系统设置项列表")
    @PostMapping(API_V1_SYS_PREFIX + "/config/list")
    public R<Page<ConfigEntity>> list(ConfigSearchRequest searchRequest) {
        return R.success(configService.findByPage(searchRequest));
    }

    @PreAuthorize("hasAnyAuthority('system:config')")
    @Operation(summary = "获取系统设置项详情")
    @ApiResponse(description = "获取系统设置项详情")
    @PostMapping(API_V1_SYS_PREFIX + "/config/details")
    public R<ConfigEntity> details(@Parameter(description = "系统设置项ID") @RequestParam("id") Long id) {
        return R.success(configService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:config')")
    @Operation(summary = "保存系统设置项")
    @ApiResponse(description = "保存系统设置项")
    @PostMapping(API_V1_SYS_PREFIX + "/config/save")
    @OperationLog("保存系统设置项")
    public R<?> save(@Valid @RequestBody ConfigSaveRequest form) {
        this.configService.saveConfig(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:config')")
    @Operation(summary = "删除系统设置项")
    @ApiResponse(description = "删除系统设置项")
    @PostMapping(API_V1_SYS_PREFIX + "/config/delete")
    @OperationLog("删除系统设置项")
    public R<?> delete(@Valid DeleteRequest request) {
        if (request != null && request.getIds() != null) {
            configService.softDeleteBatchById(Arrays.asList(request.getIds()));
        }
        return R.success();
    }
}
