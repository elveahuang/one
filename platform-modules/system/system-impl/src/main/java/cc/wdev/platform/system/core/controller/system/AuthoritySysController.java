package cc.wdev.platform.system.core.controller.system;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.core.service.AuthorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "AuthorityAdminController", description = "权限后台控制器")
public class AuthoritySysController extends AbstractController {

    private final AuthorityService authorityService;

    @PreAuthorize("hasAnyAuthority('system:authority')")
    @Operation(summary = "获取权限列表")
    @ApiResponse(description = "获取权限列表")
    @GetMapping(API_V1_SYS_PREFIX + "/authority/list")
    public R<?> list(@RequestParam(value = "entityId", required = false, defaultValue = "0") @Parameter(description = "实体ID") Long entityId,
                     @RequestParam("bizType") @Parameter(description = "业务类型") String bizType) {
        if (ObjectUtils.isValidId(entityId)) {
            return R.success(authorityService.findAuthorityIds(bizType, entityId));
        }
        return R.success(authorityService.findAuthorityVoList());
    }

}
