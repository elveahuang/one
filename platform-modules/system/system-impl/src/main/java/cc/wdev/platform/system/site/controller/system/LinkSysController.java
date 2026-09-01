package cc.wdev.platform.system.site.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.api.LinkApi;
import cc.wdev.platform.system.site.domain.form.LinkForm;
import cc.wdev.platform.system.site.domain.request.LinkSearchRequest;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "LinkAdminController", description = "友情链接控制器")
public class LinkSysController extends AbstractController {

    private final LinkApi linkApi;

    @PreAuthorize("hasAnyAuthority('system:link')")
    @Operation(summary = "获取友情链接列表")
    @ApiResponse(description = "获取友情链接列表")
    @PostMapping(API_V1_SYS_PREFIX + "/link/list")
    public R<Page<LinkVo>> list(@Parameter(description = "查询友情链接请求参数") LinkSearchRequest request) {
        return R.success(linkApi.friendLinksByKeyword(request));
    }

    @Operation(summary = "获取友情链接详情")
    @ApiResponse(description = "获取友情链接详情")
    @PostMapping(API_V1_SYS_PREFIX + "/link/details")
    @PreAuthorize("hasAnyAuthority('system:link')")
    public R<LinkVo> details(@Parameter(description = "友情链接ID") @RequestParam("id") Long id) {
        return R.success(linkApi.getLink(id));
    }


    @Operation(summary = "保存友情链接")
    @ApiResponse(description = "保存友情链接")
    @PostMapping(API_V1_SYS_PREFIX + "/link/save")
    @OperationLog("保存友情链接")
    @PreAuthorize("hasAnyAuthority('system:link')")
    public R<?> save(@RequestBody @Parameter(description = "友情链接保存表单") LinkForm linkForm) {
        linkApi.saveLink(linkForm);
        return R.success();
    }

    @Operation(summary = "删除友情链接")
    @ApiResponse(description = "删除友情链接")
    @PostMapping(API_V1_SYS_PREFIX + "/link/delete")
    @OperationLog("删除友情链接")
    @PreAuthorize("hasAnyAuthority('system:link')")
    public R<?> delete(@Parameter(description = "友情链接删除请求参数") @Valid DeleteRequest request) {
        linkApi.deleteLink(Arrays.asList(request.getIds()));
        return R.success();
    }

}
