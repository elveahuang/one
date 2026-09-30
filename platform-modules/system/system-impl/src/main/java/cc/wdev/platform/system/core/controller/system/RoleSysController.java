package cc.wdev.platform.system.core.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.IdsRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.commons.enums.RoleDataScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.RoleGroupTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.request.BizTypeSearchRequest;
import cc.wdev.platform.system.core.domain.form.RoleForm;
import cc.wdev.platform.system.core.domain.request.RoleSearchRequest;
import cc.wdev.platform.system.core.domain.vo.RoleOptionsVo;
import cc.wdev.platform.system.core.domain.vo.RoleVo;
import cc.wdev.platform.system.core.service.RoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "RoleAdminController", description = "角色后台控制器")
public class RoleSysController extends AbstractController {

    private final BizTypeApi bizTypeApi;

    private final RoleService roleService;

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "获取角色列表")
    @ApiResponse(description = "获取角色列表")
    @PostMapping(API_V1_SYS_PREFIX + "/role/list")
    public R<Page<RoleVo>> list(@Parameter(description = "角色查询请求") @Valid RoleSearchRequest request) {
        return R.success(roleService.findRolePage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "获取角色详情")
    @ApiResponse(description = "获取角色详情")
    @PostMapping(API_V1_SYS_PREFIX + "/role/details")
    public R<RoleVo> details(@Parameter(description = "角色ID") @RequestParam("id") Long id) {
        return R.success(roleService.findRoleById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "保存角色")
    @ApiResponse(description = "保存角色")
    @PostMapping(API_V1_SYS_PREFIX + "/role/save")
    @OperationLog("保存角色")
    public R<?> save(@Parameter(description = "角色保存表单") @Valid RoleForm form) {
        this.roleService.saveRole(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "保存角色权限")
    @ApiResponse(description = "保存角色权限")
    @PostMapping(API_V1_SYS_PREFIX + "/role/save-authority")
    @OperationLog("保存角色权限")
    public R<?> saveAuthority(@Parameter(description = "角色保存表单") @Valid RoleForm form) {
        this.roleService.saveRoleAuthority(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "删除角色")
    @ApiResponse(description = "删除角色")
    @PostMapping(API_V1_SYS_PREFIX + "/role/delete")
    @OperationLog("删除角色")
    public R<?> delete(@Parameter(description = "角色ID数组") @Valid IdsRequest request) {
        roleService.batchDeleteRoles(request.getIds());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "检查编号是否可用")
    @ApiResponse(description = "检查编号是否可用")
    @GetMapping(API_V1_SYS_PREFIX + "/role/check")
    public R<Boolean> checkItemCode(@Parameter(description = "角色编号") @RequestParam("code") String code,
                                    @Parameter(description = "角色ID") @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(!roleService.existsByCode(code, id));
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "获取所有角色")
    @ApiResponse(description = "获取所有角色")
    @GetMapping(API_V1_SYS_PREFIX + "/roles")
    public R<List<RoleVo>> roles() {
        return R.success(roleService.findAllRoles());
    }

    @PreAuthorize("hasAnyAuthority('system:role')")
    @Operation(summary = "获取所有参数选项")
    @ApiResponse(description = "获取所有参数选项")
    @GetMapping(API_V1_SYS_PREFIX + "/options")
    public R<RoleOptionsVo> options() {
        List<SimpleOptionVo> roleTypes = this.bizTypeApi.findBizTypeVoList(BizTypeSearchRequest.builder()
            .bizGroupType(CoreBizGroupTypeEnum.ROLE_TYPE.getValue())
            .build()
        );

        return R.success(RoleOptionsVo.builder()
            .roleDataScopeTypes(RoleDataScopeTypeEnum.getRoleDataScopeTypes())
            .roleGroupTypes(RoleGroupTypeEnum.getRoleGroupTypes())
            .roleBizTypes(roleTypes)
            .build()
        );
    }

}
