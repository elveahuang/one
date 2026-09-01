package cc.wdev.platform.system.open.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.WxMpApi;
import cc.wdev.platform.system.open.domain.form.WxMpConfigForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * 微信公众号管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "WxMpAdminController", description = "微信公众号管理控制器")
public class WxMpSysController extends AbstractController {

    private final WxMpApi wxMpApi;

    @PreAuthorize("hasAnyAuthority('open:wechat:mp:list')")
    @Operation(summary = "获取系统基本信息")
    @ApiResponse(description = "获取系统基本信息")
    @GetMapping(API_V1_SYS_PREFIX + "/oapis/wx/mp/config")
    public R<WxMpConfigForm> getBase() {
        return R.success(wxMpApi.getWxMpConfig());
    }

    @PreAuthorize("hasAnyAuthority('open:wechat:mp:list')")
    @Operation(summary = "保存系统基本信息")
    @ApiResponse(description = "保存系统基本信息")
    @OperationLog("保存系统基本信息")
    @PostMapping(API_V1_SYS_PREFIX + "/oapis/wx/mp/config")
    public R<?> postBase(@Parameter(description = "新增租户表单") @RequestBody @Valid WxMpConfigForm form) {
        wxMpApi.saveWxMpConfig(form);
        return R.success();
    }

}
