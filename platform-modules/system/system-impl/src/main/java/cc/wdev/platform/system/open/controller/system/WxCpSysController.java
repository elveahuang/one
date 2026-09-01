package cc.wdev.platform.system.open.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.WxCpApi;
import cc.wdev.platform.system.open.domain.form.WxCpConfigForm;
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
 * 企业微信管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "WxCpAdminController", description = "企业微信管理控制器")
public class WxCpSysController extends AbstractController {

    private final WxCpApi wxCpApi;

    /**
     * 获取系统基本信息
     */
    @PreAuthorize("hasAnyAuthority('open:wechat:cp')")
    @Operation(summary = "获取系统基本信息")
    @ApiResponse(description = "获取系统基本信息")
    @GetMapping(API_V1_SYS_PREFIX + "/oapis/wx/cp/config")
    public R<WxCpConfigForm> getBase() {
        return R.success(wxCpApi.getWxCpConfig());
    }

    /**
     * 保存系统基本信息
     */
    @PreAuthorize("hasAnyAuthority('open:wechat:cp')")
    @Operation(summary = "保存系统基本信息")
    @ApiResponse(description = "保存系统基本信息")
    @OperationLog("保存系统基本信息")
    @PostMapping(API_V1_SYS_PREFIX + "/oapis/wx/cp/config")
    public R<?> postBase(@Parameter(description = "企业微信配置表单") @RequestBody @Valid WxCpConfigForm form) {
        wxCpApi.saveWxCpConfig(form);
        return R.success();
    }

}
