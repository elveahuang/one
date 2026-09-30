package cc.wdev.platform.system.site.controller.webapp;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.site.domain.request.BannerSearchRequest;
import cc.wdev.platform.system.site.domain.vo.webapp.BannerWebappVo;
import cc.wdev.platform.system.site.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_WEB_PREFIX;


/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "BannerAppController", description = "宣传栏控制器")
public class BannerWebController extends AbstractController {

    private final BannerService bannerService;

    @Anonymous
    @Operation(summary = "获取宣传栏列表")
    @ApiResponse(description = "获取宣传栏列表")
    @PostMapping(API_V1_WEB_PREFIX + "/banner/list")
    public R<Page<BannerWebappVo>> list(@Parameter(description = "宣传栏查询参数") BannerSearchRequest request) {
        return R.success(bannerService.findBannerForWebapp(request));
    }

    @Anonymous
    @Operation(summary = "获取宣传栏详情")
    @ApiResponse(description = "获取宣传栏详情")
    @PostMapping(API_V1_WEB_PREFIX + "/banner/details")
    public R<BannerWebappVo> details(@Parameter(description = "宣传栏ID") @RequestParam("id") Long id) {
        return R.success(bannerService.getWebappBanner(id));
    }
}
