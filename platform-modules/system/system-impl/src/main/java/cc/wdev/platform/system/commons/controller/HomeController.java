package cc.wdev.platform.system.commons.controller;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.platform.Platform;
import cc.wdev.platform.commons.utils.platform.PlatformHelper;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author elvea
 */
@Controller
@RequestMapping("/")
@Tag(name = "HomeController", description = "首页控制器")
public class HomeController extends AbstractController {

    @PermitAll
    @Operation(summary = "首页")
    @ApiResponse(description = "首页")
    @GetMapping("/")
    @OperationLog("首页")
    public void index(HttpServletResponse response) throws Exception {
        String page;
        Platform platform = PlatformHelper.fromServletRequest();
        if (platform.isMobile()) {
            page = StringUtils.isNotEmpty(this.context.getHome().getMobile()) ? this.context.getHome().getMobile() : "";
        } else {
            page = StringUtils.isNotEmpty(this.context.getHome().getWebapp()) ? this.context.getHome().getWebapp() : "";
        }
        if (StringUtils.isEmpty(page)) {
            page = StringUtils.isNotEmpty(this.context.getHome().getMain()) ? this.context.getHome().getMain() : "home";
        }
        response.sendRedirect(page);
    }

    @PermitAll
    @Operation(summary = "关于页")
    @ApiResponse(description = "关于页")
    @GetMapping("/about")
    @OperationLog("关于页")
    public String about() {
        return "about";
    }

}
