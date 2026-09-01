package cc.wdev.platform.system.core.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.IdRequest;
import cc.wdev.platform.commons.web.request.IdsRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.core.domain.form.AddTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageAuthorityForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageForm;
import cc.wdev.platform.system.core.domain.request.TenantPackageSearchRequest;
import cc.wdev.platform.system.core.domain.vo.TenantPackageVo;
import cc.wdev.platform.system.core.service.TenantPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

@RestController
@RequiredArgsConstructor
@Tag(name = "TenantPackageAdminController", description = "租户套餐后台管理控制器")
public class TenantPackagePltController extends AbstractController {

    private final TenantPackageService tenantPackageService;

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "检查租户编号是否可用")
    @ApiResponse(description = "检查租户编号是否可用")
    @GetMapping(API_V1_SYS_PREFIX + "/tenant-package/check-code")
    public R<Boolean> checkCode(@Parameter(description = "租户编号") @RequestParam("code") String code,
                                @Parameter(description = "套餐ID") @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(!tenantPackageService.existsByCode(code, id));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "获取租户套餐列表")
    @ApiResponse(description = "获取租户套餐列表")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/list")
    public R<Page<TenantPackageVo>> list(@Parameter(description = "租户套餐查询请求") @Valid TenantPackageSearchRequest request) {
        return R.success(tenantPackageService.findPackagePage(request));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "获取套餐详情")
    @ApiResponse(description = "获取套餐详情")
    @GetMapping(API_V1_SYS_PREFIX + "/tenant-package/details")
    public R<TenantPackageVo> detail(@Parameter(description = "套餐ID") @RequestParam("id") Long id) {
        return R.success(tenantPackageService.findPackageById(id));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "新增套餐")
    @ApiResponse(description = "新增套餐")
    @OperationLog("新增套餐")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/add")
    public R<?> add(@Parameter(description = "新增套餐请求") @RequestBody @Valid AddTenantPackageForm form) throws Exception {
        tenantPackageService.addPackage(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "编辑套餐")
    @ApiResponse(description = "编辑套餐")
    @OperationLog("编辑套餐")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/edit")
    public R<?> edit(@Parameter(description = "编辑套餐请求") @RequestBody @Valid EditTenantPackageForm form) throws Exception {
        tenantPackageService.editPackage(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "套餐关联权限")
    @ApiResponse(description = "套餐关联权限")
    @OperationLog("套餐关联权限")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/edit-authority")
    public R<?> bindPackage(@Parameter(description = "套餐关联权限请求") @RequestBody @Valid EditTenantPackageAuthorityForm form) {
        tenantPackageService.editPackageAuthority(form);
        return R.success();
    }

    @Operation(summary = "获取套餐权限列表")
    @ApiResponse(description = "获取套餐权限列表")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/authority")
    public R<?> authorityByIdList(@Parameter(description = "获取套餐权限列表请求") @Valid IdRequest request) {
        return R.success(tenantPackageService.findAuthorityIdsByPackageId(request.getId()));
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "删除租户套餐")
    @ApiResponse(description = "删除租户套餐")
    @OperationLog("删除租户套餐")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/delete")
    public R<?> delete(@Parameter(description = "删除租户套餐请求") @Valid IdRequest request) throws Exception {
        tenantPackageService.deletePackage(request.getId());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:tenant:package')")
    @Operation(summary = "批量删除租户套餐")
    @ApiResponse(description = "批量删除租户套餐")
    @OperationLog("批量删除租户套餐")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant-package/batch-delete")
    public R<?> batchDelete(@Parameter(description = "批量删除租户套餐请求") @Valid IdsRequest request) {
        tenantPackageService.batchDeletePackage(request.getIds());
        return R.success();
    }
}
