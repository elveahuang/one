package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.oapis.dingtalk.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.dingtalk.service.DingTalkService;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.DingtalkApi;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;
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
@Tag(name = "DingTalkController", description = "钉钉控制器")
public class DingTalkWebController extends AbstractController {

    private DingTalkService dingTalkService;

    private final DingtalkApi dingtalkApi;

    @PermitAll
    @Operation(summary = "获取钉钉签名")
    @ApiResponse(description = "获取钉钉签名")
    @ResponseBody
    @GetMapping(API_V1_PREFIX + "/dingtalk/signature")
    public R<JsapiSignature> getSignature(@Parameter(description = "请求url") @RequestBody String url) throws Exception {
        log.info("get feishu signature for url [{}]", url);
        return R.success(this.dingTalkService.createJsapiSignature(url));
    }

    @PermitAll
    @Operation(summary = "钉钉回调接口")
    @ApiResponse(description = "钉钉回调接口")
    @GetMapping(API_V1_PREFIX + "/dingtalk/callback")
    public R<?> callback() {
        return R.success();
    }

    @PermitAll
    @Operation(summary = "获取钉钉配置")
    @ApiResponse(description = "获取钉钉配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/dingtalk/config")
    public R<DingtalkAppVo> getConfig() {
        return R.success(this.dingtalkApi.getDingtalkApp());
    }
}
