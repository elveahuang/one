package cc.wdev.platform.system.security.controller.platform;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.security.service.AuthorizationConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "AuthorizationConsentAdminController", description = "客户端管理控制器")
public class AuthorizationConsentPltController extends AbstractController {

    private final AuthorizationConsentService authorizationConsentService;

    @PreAuthorize("hasAnyAuthority('system:session')")
    @Operation(summary = "获取客户端列表")
    @ApiResponse(description = "获取客户端列表")
    @GetMapping(API_V1_SYS_PREFIX + "/authorization-consent/list")
    public R<?> list(@Parameter(description = "分页参数") PageRequest pageRequest) {
        return R.success(authorizationConsentService.findByPage(pageRequest.getPageable()));
    }

}
