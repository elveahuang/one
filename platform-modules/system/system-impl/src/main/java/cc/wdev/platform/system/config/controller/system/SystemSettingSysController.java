package cc.wdev.platform.system.config.controller.system;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.mail.MailConfig;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.oapis.location.LocationConfig;
import cc.wdev.platform.commons.oapis.sms.SmsConfig;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.domain.form.AppBaseSettingForm;
import cc.wdev.platform.system.config.domain.form.AppPageForm;
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

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author irving
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "SystemAdminController", description = "系统管理和设置控制器")
public class SystemSettingSysController extends AbstractController {

    private final ConfigApi configApi;

    @Authenticated
    @Operation(summary = "获取系统基本信息")
    @ApiResponse(description = "获取系统基本信息")
    @GetMapping(API_V1_SYS_PREFIX + "/system-setting/base")
    public R<AppBaseSettingForm> getBase() {
        return R.success(this.configApi.getAppBaseInfo());
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "保存系统基本信息")
    @ApiResponse(description = "保存系统基本信息")
    @OperationLog("保存系统基本信息")
    @PostMapping(API_V1_SYS_PREFIX + "/system-setting/base")
    public R<?> postBase(@Parameter(description = "新增租户表单") @RequestBody @Valid AppBaseSettingForm form) {
        this.configApi.saveAppBaseInfo(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "获取系统页面列表")
    @ApiResponse(description = "获取系统页面列表")
    @GetMapping(API_V1_SYS_PREFIX + "/system-setting/pages")
    public R<List<AppPageForm>> getPages() {
        return R.success(this.configApi.getPages());
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "保存页面")
    @ApiResponse(description = "保存页面")
    @OperationLog("保存页面")
    @PostMapping(API_V1_SYS_PREFIX + "/system-setting/page/save")
    public R<?> postPage(@Parameter(description = "页面内容表单") @RequestBody @Valid AppPageForm form) {
        this.configApi.savePage(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "获取系统协议列表")
    @ApiResponse(description = "获取系统协议列表")
    @GetMapping(API_V1_SYS_PREFIX + "/system-setting/agreements")
    public R<List<AppPageForm>> getAgreements() {
        return R.success(this.configApi.getAgreements());
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "保存协议")
    @ApiResponse(description = "保存协议")
    @OperationLog("保存协议")
    @PostMapping(API_V1_SYS_PREFIX + "/system-setting/agreement/save")
    public R<?> postAgreement(@Parameter(description = "保存协议内容表单") @RequestBody @Valid AppPageForm form) {
        this.configApi.saveAgreement(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "获取邮件服务器配置")
    @ApiResponse(description = "获取邮件服务器配置")
    @GetMapping(API_V1_SYS_PREFIX + "/system-setting/mail")
    public R<MailConfig> getMailConfig() {
        return R.success(this.configApi.getMailConfig());
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "保存邮件服务器配置")
    @ApiResponse(description = "保存邮件服务器配置")
    @OperationLog("保存邮件服务器配置")
    @PostMapping(API_V1_SYS_PREFIX + "/system-setting/mail")
    public R<?> postMailConfig(@Parameter(description = "邮件服务器配置表单") @RequestBody @Valid MailConfig form) {
        this.configApi.saveMailConfig(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "获取短信服务器配置")
    @ApiResponse(description = "获取短信服务器配置")
    @GetMapping(API_V1_SYS_PREFIX + "/system-setting/sms")
    public R<SmsConfig> getSmsConfig() {
        return R.success(this.configApi.getSmsConfig());
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "保存短信服务器配置")
    @ApiResponse(description = "保存短信服务器配置")
    @OperationLog("保存短信服务器配置")
    @PostMapping(API_V1_SYS_PREFIX + "/system-setting/sms")
    public R<?> postSmsConfig(@Parameter(description = "短信服务器配置表单") @RequestBody @Valid SmsConfig form) {
        this.configApi.saveSmsConfig(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "获取位置服务配置")
    @ApiResponse(description = "获取位置服务配置")
    @GetMapping(API_V1_SYS_PREFIX + "/system-setting/location")
    public R<LocationConfig> getLocationConfig() {
        return R.success(this.configApi.getLocationConfig());
    }

    @PreAuthorize("hasAnyAuthority('system:base')")
    @Operation(summary = "保存位置服务配置")
    @ApiResponse(description = "保存位置服务配置")
    @OperationLog("保存位置服务配置")
    @PostMapping(API_V1_SYS_PREFIX + "/system-setting/location")
    public R<?> postApikeyConfig(@Parameter(description = "位置服务配置表单") @RequestBody @Valid LocationConfig form) {
        this.configApi.saveLocationConfig(form);
        return R.success();
    }

}
