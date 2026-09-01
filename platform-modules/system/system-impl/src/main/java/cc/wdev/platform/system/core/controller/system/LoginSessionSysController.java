package cc.wdev.platform.system.core.controller.system;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.core.domain.entity.LoginSessionEntity;
import cc.wdev.platform.system.core.domain.request.LoginSessionSearchRequest;
import cc.wdev.platform.system.core.service.LoginSessionService;
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
@Tag(name = "LoginSessionAdminController", description = "用户会话后台控制器")
public class LoginSessionSysController extends AbstractController {

    private final LoginSessionService loginSessionService;

    @PreAuthorize("hasAnyAuthority('system:session')")
    @Operation(summary = "获取用户登陆列表")
    @ApiResponse(description = "获取用户登陆列表")
    @PostMapping(API_V1_SYS_PREFIX + "/login-session/list")
    public R<Page<LoginSessionEntity>> user(LoginSessionSearchRequest request) {
        return R.success(this.loginSessionService.findLoginSessionList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:session')")
    @Operation(summary = "获取用户登陆详情")
    @ApiResponse(description = "获取在线用户详情")
    @PostMapping(API_V1_SYS_PREFIX + "/login-session/detail")
    public R<LoginSessionEntity> details(@Parameter(description = "登陆会话ID") @RequestParam("id") Long id) {
        return R.success(loginSessionService.findById(id));
    }

}
