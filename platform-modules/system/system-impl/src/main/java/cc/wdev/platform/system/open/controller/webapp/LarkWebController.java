package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.oapis.lark.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.lark.service.LarkService;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.LarkApi;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;
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
@Tag(name = "LarkController", description = "飞书控制器")
public class LarkWebController extends AbstractController {

    private LarkService larkService;

    private final LarkApi larkApi;

    @PermitAll
    @Operation(summary = "获取飞书配置")
    @ApiResponse(description = "获取飞书配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/lark/config")
    public R<LarkAppVo> getConfig() {
        return R.success(this.larkApi.getLarkApp());
    }

    @PermitAll
    @Operation(summary = "获取飞书签名")
    @ApiResponse(description = "获取飞书签名")
    @ResponseBody
    @GetMapping(API_V1_PREFIX + "/lark/signature")
    public R<JsapiSignature> getSignature(@RequestBody @Parameter(description = "飞书回调URL") String url) throws Exception {
        log.info("get feishu signature for url [{}]", url);
        return R.success(this.larkService.createJsapiSignature(url));
    }

    @PermitAll
    @OperationLog("飞书回调接口")
    @Operation(summary = "飞书回调接口")
    @ApiResponse(description = "飞书回调接口")
    @GetMapping(API_V1_PREFIX + "/lark/callback")
    public R<?> callback() {
        return R.success();
    }

}
