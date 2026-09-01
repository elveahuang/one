package cc.wdev.platform.system.core.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.IdRequest;
import cc.wdev.platform.commons.web.request.IdsRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.core.api.TenantApi;
import cc.wdev.platform.system.core.domain.form.AddTenantForm;
import cc.wdev.platform.system.core.domain.form.BindTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantForm;
import cc.wdev.platform.system.core.domain.request.TenantSearchRequest;
import cc.wdev.platform.system.core.domain.request.TenantUpdateExpirationDateRequest;
import cc.wdev.platform.system.core.domain.vo.TenantPackageVo;
import cc.wdev.platform.system.core.domain.vo.TenantVo;
import cc.wdev.platform.system.core.service.TenantPackageService;
import cc.wdev.platform.system.core.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

@RestController
@RequiredArgsConstructor
@Tag(name = "TenantAdminController", description = "租户后台管理控制器")
public class TenantPltController extends AbstractController {

    private final TenantService tenantService;

    private final TenantPackageService tenantPackageService;

    private final TenantApi tenantApi;

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "检查租户编号是否可用")
    @ApiResponse(description = "检查租户编号是否可用")
    @GetMapping(API_V1_SYS_PREFIX + "/tenant/check-code")
    public R<Boolean> checkCode(@Parameter(description = "租户编号") @RequestParam("code") String code,
                                @Parameter(description = "租户ID") @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(!tenantService.existsByCode(code, id));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "获取租户列表")
    @ApiResponse(description = "获取租户列表")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/list")
    public R<Page<TenantVo>> list(@Parameter(description = "查询参数") @Valid TenantSearchRequest request) {
        return R.success(tenantService.findTenantPage(request));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "获取租户详情")
    @ApiResponse(description = "获取租户详情")
    @GetMapping(API_V1_SYS_PREFIX + "/tenant/details")
    public R<TenantVo> detail(@Parameter(description = "租户ID") @RequestParam("id") Long id) {
        return R.success(tenantService.findTenantById(id));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "新增租户")
    @ApiResponse(description = "新增租户")
    @OperationLog("新增租户")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/add")
    public R<?> add(@Parameter(description = "新增租户表单") @RequestBody @Valid AddTenantForm form) throws Exception {
        tenantService.addTenant(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "编辑租户")
    @ApiResponse(description = "编辑租户")
    @OperationLog("编辑租户")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/edit")
    public R<?> edit(@Parameter(description = "编辑租户表单") @RequestBody @Valid EditTenantForm form) throws Exception {
        tenantService.editTenant(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "获取租户套餐下拉项")
    @ApiResponse(description = "获取租户套餐下拉项")
    @GetMapping(API_V1_SYS_PREFIX + "/tenant/package-list")
    public R<List<TenantPackageVo>> packageList() {
        return R.success(tenantPackageService.list());
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "删除租户")
    @ApiResponse(description = "删除租户")
    @OperationLog("删除租户")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/delete")
    public R<?> delete(@Parameter(description = "租户ID") @Valid IdRequest request) throws Exception {
        tenantService.deleteTenant(request.getId());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "批量删除租户")
    @ApiResponse(description = "批量删除租户")
    @OperationLog("批量删除租户")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/batch-delete")
    public R<?> batchDelete(@Parameter(description = "租户ID数组") @Valid IdsRequest request) {
        tenantService.batchDeleteTenant(request.getIds());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "租户绑定套餐")
    @ApiResponse(description = "租户绑定套餐")
    @OperationLog("租户绑定套餐")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/bind-package")
    public R<?> bindPackage(@Parameter(description = "绑定租户套餐表单") @Valid BindTenantPackageForm request) {
        tenantService.bindPackage(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "同步租户套餐")
    @ApiResponse(description = "同步租户套餐")
    @OperationLog("同步租户套餐")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/sync-package")
    public R<?> syncPackage(@Parameter(description = "租户ID") @Valid IdRequest request) {
        tenantService.syncPackage(request.getId());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "启用租户")
    @ApiResponse(description = "启用租户")
    @OperationLog("启用租户")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/enable")
    public R<?> enable(@Parameter(description = "租户ID") @Valid IdRequest request) {
        tenantService.enableTenant(request.getId());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "禁用租户")
    @ApiResponse(description = "禁用租户")
    @OperationLog("禁用租户")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/disable")
    public R<?> disable(@Parameter(description = "租户ID") @Valid IdRequest request) {
        tenantService.disableTenant(request.getId());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "修改到期时间")
    @ApiResponse(description = "修改到期时间")
    @OperationLog("修改到期时间")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/update-expiration")
    public R<?> updateExpirationDate(@Parameter(description = "租户更新到期时间请求") @RequestBody @Valid TenantUpdateExpirationDateRequest request) {
        tenantService.updateExpirationDate(request.getTenantId(), request.getExpirationDate());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant')")
    @Operation(summary = "租户列表")
    @ApiResponse(description = "租户列表")
    @OperationLog("租户列表")
    @GetMapping(API_V1_SYS_PREFIX + "/tenant/filter")
    public R<List<TenantVo>> filter() {
        return R.success(tenantApi.filter());
    }
}
