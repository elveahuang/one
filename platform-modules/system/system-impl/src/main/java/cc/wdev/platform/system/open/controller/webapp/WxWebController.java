package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@RestController
@Tag(name = "WxController", description = "微信控制器")
public class WxWebController extends AbstractController {

    /**
     * 这个接口是完全开放的，允许微信公众号配置的时候，做域名验证用
     */
    @Operation(summary = "微信域名验证")
    @ApiResponse(description = "微信域名验证")
    @GetMapping("/MP_verify_{text}.txt")
    public String verify(@PathVariable(name = "text") String value) {
        return nvl(value).trim();
    }

}
