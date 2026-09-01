package cc.wdev.platform.system.commons.controller;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.annotations.Super;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.commons.api.CoreApi;
import cc.wdev.platform.system.commons.domain.vo.InitializeVo;
import cc.wdev.platform.system.commons.domain.vo.PageVo;
import cc.wdev.platform.system.config.enums.ConfigBizTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@Controller
@AllArgsConstructor
@Tag(name = "DefaultController", description = "默认控制器")
public class DefaultController {

    private final CoreApi coreApi;

    @Anonymous
    @GetMapping(API_V1_PREFIX + "/agreement")
    public String agreement(Model model) {
        PageVo page = coreApi.getPage(ConfigBizTypeEnum.APP_AGREEMENT_USER.getValue());
        model.addAttribute("content", page.getContent());
        return "agreement";
    }

    @Anonymous
    @GetMapping(API_V1_PREFIX + "/privacy")
    public String privacy(Model model) {
        PageVo page = coreApi.getPage(ConfigBizTypeEnum.APP_AGREEMENT_PRIVACY_POLICY.getValue());
        model.addAttribute("content", page.getContent());
        return "privacy";
    }

    @Anonymous
    @ResponseBody
    @Operation(summary = "获取应用初始数据")
    @ApiResponse(description = "获取应用初始数据")
    @PostMapping(API_V1_PREFIX + "/initialize")
    public R<InitializeVo> initialize() {
        return R.success(coreApi.initialize());
    }

    @Anonymous
    @ResponseBody
    @OperationLog("首页")
    @Operation(summary = "首页")
    @ApiResponse(description = "首页")
    @PostMapping(API_V1_PREFIX + "/home")
    public R<InitializeVo> home() {
        return R.success(coreApi.initialize());
    }

    @Anonymous
    @ResponseBody
    @OperationLog("首页")
    @Operation(summary = "首页")
    @ApiResponse(description = "首页")
    @PostMapping(API_V1_PREFIX + "/pages/{code}")
    public R<PageVo> pages(@PathVariable String code) {
        return R.success(coreApi.getPage(code));
    }

    @Super
    @ResponseBody
    @OperationLog("系统基本数据初始化")
    @Operation(summary = "系统基本数据初始化")
    @ApiResponse(description = "系统基本数据初始化")
    @PostMapping(API_V1_PREFIX + "/setup/initialize")
    public R<Void> setup() {
        coreApi.setup();
        return R.success();
    }

}
