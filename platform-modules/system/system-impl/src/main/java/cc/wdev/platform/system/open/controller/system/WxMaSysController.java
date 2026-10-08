package cc.wdev.platform.system.open.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.WxMaApi;
import cc.wdev.platform.system.open.domain.form.WxMaConfigForm;
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
 * 微信小程序管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "WxMaAdminController", description = "微信小程序管理控制器")
public class WxMaSysController extends AbstractController {

    private final WxMaApi wxMaApi;

    /**
     * 获取小程序基本信息
     */
    @PreAuthorize("hasAnyAuthority('open:wechat:ma')")
    @Operation(summary = "获取小程序基本信息")
    @ApiResponse(description = "获取小程序基本信息")
    @GetMapping(API_V1_SYS_PREFIX + "/oapis/wx/ma/config")
    public R<WxMaConfigForm> getBase() {
        return R.success(wxMaApi.getWxMaConfig());
    }

    /**
     * 保存小程序基本信息
     */
    @PreAuthorize("hasAnyAuthority('open:wechat:ma')")
    @Operation(summary = "保存小程序基本信息")
    @ApiResponse(description = "保存小程序基本信息")
    @OperationLog("保存小程序基本信息")
    @PostMapping(API_V1_SYS_PREFIX + "/oapis/wx/ma/config")
    public R<?> postBase(@Parameter(description = "小程序配置表单") @RequestBody @Valid WxMaConfigForm form) {
        wxMaApi.saveWxMaConfig(form);
        return R.success();
    }

}
