package cc.wdev.platform.system.site.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.api.BannerApi;
import cc.wdev.platform.system.site.domain.form.BannerForm;
import cc.wdev.platform.system.site.domain.request.BannerSearchRequest;
import cc.wdev.platform.system.site.domain.vo.BannerVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "BannerAdminController", description = "宣传栏管理端控制器")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "成功"),
    @ApiResponse(responseCode = "400", description = "请求参数错误")
})
public class BannerSysController extends AbstractController {

    private final BannerApi bannerApi;

    @PreAuthorize("hasAnyAuthority('system:banner')")
    @Operation(summary = "获取宣传栏列表")
    @ApiResponse(description = "获取宣传栏列表")
    @PostMapping(API_V1_SYS_PREFIX + "/banner/list")
    public R<Page<BannerVo>> list(@Parameter(description = "宣传栏查询参数") BannerSearchRequest request) {
        return R.success(bannerApi.findPageBanner(request));
    }

    @PreAuthorize("hasAnyAuthority('system:banner')")
    @Operation(summary = "获取宣传栏详情")
    @ApiResponse(description = "获取宣传栏详情")
    @PostMapping(API_V1_SYS_PREFIX + "/banner/details")
    public R<BannerVo> details(@RequestParam("id") @Parameter(description = "宣传栏ID") Long id) {
        return R.success(bannerApi.getBanner(id));
    }

    @PreAuthorize("hasAnyAuthority('system:banner')")
    @Operation(summary = "保存宣传栏")
    @ApiResponse(description = "保存宣传栏")
    @PostMapping(API_V1_SYS_PREFIX + "/banner/save")
    @OperationLog("保存宣传栏")
    public R<?> save(@RequestBody @Parameter(description = "宣传栏保存表单") @Valid BannerForm bannerForm) {
        bannerApi.saveBanner(bannerForm);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:banner')")
    @Operation(summary = "删除宣传栏")
    @ApiResponse(description = "删除宣传栏")
    @PostMapping(API_V1_SYS_PREFIX + "/banner/delete")
    @OperationLog("删除宣传栏")
    public R<?> delete(@Valid @Parameter(description = "删除参数") DeleteRequest request) {
        if (request != null && request.getIds() != null && request.getIds().length > 0) {
            bannerApi.deleteBanner(Arrays.asList(request.getIds()));
        }
        return R.success();
    }

}
