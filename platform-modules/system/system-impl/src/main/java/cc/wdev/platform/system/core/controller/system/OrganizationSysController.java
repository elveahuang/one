package cc.wdev.platform.system.core.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.IdsRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.core.api.OrganizationApi;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.request.OrganizationSearchRequest;
import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import cc.wdev.platform.system.core.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "OrganizationAdminController", description = "部门后台控制器")
public class OrganizationSysController extends AbstractController {

    private final OrganizationApi organizationApi;

    private final OrganizationService organizationService;

    @PreAuthorize("hasAnyAuthority('system:organization')")
    @Operation(summary = "获取部门树")
    @ApiResponse(description = "获取部门树")
    @PostMapping(API_V1_SYS_PREFIX + "/organization/tree")
    public R<List<OrganizationVo>> tree() {
        return R.success(organizationApi.findOrganizationTree());
    }

    @PreAuthorize("hasAnyAuthority('system:organization')")
    @Operation(summary = "获取部门列表")
    @ApiResponse(description = "获取部门列表")
    @PostMapping(API_V1_SYS_PREFIX + "/organization/list")
    public R<Page<OrganizationVo>> list(@Parameter(description = "部门查询请求") @Valid OrganizationSearchRequest request) {
        return R.success(organizationApi.findOrganizationPage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:organization')")
    @Operation(summary = "获取部门详情")
    @ApiResponse(description = "获取部门详情")
    @PostMapping(API_V1_SYS_PREFIX + "/organization/details")
    public R<OrganizationVo> details(@Parameter(description = "部门ID") @RequestParam("id") Long id) {
        return R.success(organizationApi.findOrganizationDetails(id));
    }

    @PreAuthorize("hasAnyAuthority('system:organization')")
    @Operation(summary = "保存部门")
    @ApiResponse(description = "保存部门")
    @PostMapping(API_V1_SYS_PREFIX + "/organization/save")
    @OperationLog("保存部门")
    public R<?> save(@Parameter(description = "部门保存请求") @Valid @RequestBody OrganizationSaveDto saveDto) {
        return R.success(organizationApi.saveOrganization(saveDto));
    }

    @PreAuthorize("hasAnyAuthority('system:organization')")
    @Operation(summary = "删除部门")
    @ApiResponse(description = "删除部门")
    @PostMapping(API_V1_SYS_PREFIX + "/organization/delete")
    @OperationLog("删除部门")
    public R<?> delete(@Parameter(description = "部门ID数组") @Valid IdsRequest request) {
        organizationApi.deleteOrganization(request.getIds());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:organization')")
    @Operation(summary = "检查编号是否可用")
    @ApiResponse(description = "检查编号是否可用")
    @GetMapping(API_V1_SYS_PREFIX + "/organization/check")
    public R<Boolean> checkCode(@Parameter(description = "部门编号") @RequestParam("code") String code,
                                @Parameter(description = "部门ID") @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(!organizationService.existsByCode(code, id));
    }

}
