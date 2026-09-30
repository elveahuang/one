package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.WxCpApi;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "WxCpController", description = "企业微信控制器")
public class WxCpWebController extends AbstractController {

    private final WxCpApi wxCpApi;

    @PermitAll
    @Operation(summary = "获取企业微信配置")
    @ApiResponse(description = "获取企业微信配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/wx/cp/config")
    public R<WxCpAppVo> getConfig() {
        return R.success(this.wxCpApi.getWxCpApp());
    }

    @PermitAll
    @Operation(summary = "获取企业微信签名")
    @ApiResponse(description = "获取企业微信签名")
    @ResponseBody
    @GetMapping(API_V1_PREFIX + "/wx/cp/signature")
    public R<?> getSignature(@Parameter(description = "请求URL") @RequestBody String url) {
        return R.success();
    }

    @PermitAll
    @OperationLog("企业微信回调接口")
    @Operation(summary = "企业微信回调接口")
    @ApiResponse(description = "企业微信回调接口")
    @GetMapping(API_V1_PREFIX + "/wx/cp/callback")
    public R<?> callback() {
        return R.success();
    }

}
