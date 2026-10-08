package cc.wdev.platform.system.site.controller.webapp;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.site.domain.request.LinkSearchRequest;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import cc.wdev.platform.system.site.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_WEB_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "LinkAppController", description = "短链接控制器")
public class LinkWebController extends AbstractController {

    private final LinkService linkService;

    @Anonymous
    @Operation(summary = "获取友情链接列表")
    @ApiResponse(description = "获取友情链接列表")
    @PostMapping(API_V1_WEB_PREFIX + "/link/list")
    public R<Page<LinkVo>> list(@Parameter(description = "友情链接分页请求") LinkSearchRequest request) {
        return R.success(linkService.friendLinkList(request));
    }

}
