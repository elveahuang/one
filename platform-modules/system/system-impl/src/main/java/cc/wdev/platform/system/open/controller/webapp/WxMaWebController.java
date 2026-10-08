package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.WxMaApi;
import cc.wdev.platform.system.open.domain.vo.WxMaAppVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "WxMaController", description = "微信小程序控制器")
public class WxMaWebController extends AbstractController {

    private final WxMaApi wxMaApi;

    @PermitAll
    @Operation(summary = "获取微信小程序配置")
    @ApiResponse(description = "获取微信小程序配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/wx/ma/config")
    public R<WxMaAppVo> getConfig() {
        return R.success(this.wxMaApi.getWxMaApp());
    }
}
