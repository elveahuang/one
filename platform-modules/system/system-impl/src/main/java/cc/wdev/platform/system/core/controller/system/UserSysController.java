package cc.wdev.platform.system.core.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ArrayUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.dto.UserInfoDto;
import cc.wdev.platform.system.core.domain.form.UserForm;
import cc.wdev.platform.system.core.domain.request.UserSearchRequest;
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

import java.util.Arrays;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "UserAdminController", description = "用户后台控制器")
public class UserSysController extends AbstractController {

    private final UserApi userApi;

    @PreAuthorize("hasAnyAuthority('system:user')")
    @Operation(summary = "获取用户列表")
    @ApiResponse(description = "获取用户列表")
    @PostMapping(API_V1_SYS_PREFIX + "/user/list")
    public R<Page<UserInfoDto>> list(UserSearchRequest searchRequest) {
        return R.success(this.userApi.findUserPage(searchRequest));
    }

    @PreAuthorize("hasAnyAuthority('system:user')")
    @Operation(summary = "获取用户详情")
    @ApiResponse(description = "获取用户详情")
    @PostMapping(API_V1_SYS_PREFIX + "/user/details")
    public R<UserInfoDto> details(@Parameter(description = "用户ID") @RequestParam("id") Long id) {
        return R.success(this.userApi.findUserDetails(id));
    }

    @PreAuthorize("hasAnyAuthority('system:user')")
    @Operation(summary = "保存用户资讯")
    @ApiResponse(description = "保存用户资讯")
    @PostMapping(API_V1_SYS_PREFIX + "/user/save")
    @OperationLog("保存用户资讯")
    public R<?> save(@Valid UserForm form) throws Exception {
        if ((form.getId() == null || form.getId() == 0) && StringUtils.isEmpty(form.getPassword())) {
            throw new ServiceException(ResponseCodeEnum.USER_PASSWORD_NOT_AVAILABLE);
        }
        if (!StringUtils.isEmpty(form.getPassword())) {
            form.setPassword(SecurityUtils.encode(form.getPassword()));
        }
        this.userApi.saveUser(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:user')")
    @Operation(summary = "修改用户密码")
    @ApiResponse(description = "修改用户密码")
    @PostMapping(API_V1_SYS_PREFIX + "/user/reset/password")
    @OperationLog("修改用户")
    public R<?> resetPassword(UserForm form) throws Exception {
        if (form.getId() != null && form.getId() > 0 && !StringUtils.isEmpty(form.getPassword())) {
            form.setPassword(SecurityUtils.encode(form.getPassword()));
        } else {
            throw new ServiceException(ResponseCodeEnum.USER__INVALID_USERNAME_OR_PASSWORD);
        }
        this.userApi.saveUser(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:user')")
    @Operation(summary = "删除用户资讯")
    @ApiResponse(description = "删除用户资讯")
    @PostMapping(API_V1_SYS_PREFIX + "/user/delete")
    @OperationLog("删除用户资讯")
    public R<?> delete(@Valid DeleteRequest request) {
        if (ArrayUtils.isNotEmpty(request.getIds())) {
            this.userApi.deleteUser(Arrays.asList(request.getIds()));
        }
        return R.success();
    }

}
