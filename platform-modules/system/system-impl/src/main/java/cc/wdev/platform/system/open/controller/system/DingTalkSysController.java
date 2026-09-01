package cc.wdev.platform.system.open.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.DingtalkApi;
import cc.wdev.platform.system.open.domain.form.DingtalkConfigForm;
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
 * 钉钉管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "DingTalkAdminController", description = "钉钉管理控制器")
public class DingTalkSysController extends AbstractController {

    private final DingtalkApi dingtalkApi;

    /**
     * 获取系统基本信息
     */
    @PreAuthorize("hasAnyAuthority('open:dingtalk')")
    @Operation(summary = "获取系统基本信息")
    @ApiResponse(description = "获取系统基本信息")
    @GetMapping(API_V1_SYS_PREFIX + "/oapis/dingtalk/config")
    public R<DingtalkConfigForm> getBase() {
        return R.success(dingtalkApi.getDingtalkConfig());
    }

    /**
     * 保存系统基本信息
     */
    @PreAuthorize("hasAnyAuthority('open:dingtalk')")
    @Operation(summary = "保存系统基本信息")
    @ApiResponse(description = "保存系统基本信息")
    @OperationLog("保存系统基本信息")
    @PostMapping(API_V1_SYS_PREFIX + "/oapis/dingtalk/config")
    public R<?> postBase(@Parameter(description = "钉钉配置表单") @RequestBody @Valid DingtalkConfigForm form) {
        dingtalkApi.saveDingtalkConfig(form);
        return R.success();
    }

}
