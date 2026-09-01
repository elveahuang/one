package cc.wdev.platform.system.site.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.api.KeywordApi;
import cc.wdev.platform.system.site.domain.form.KeywordForm;
import cc.wdev.platform.system.site.domain.request.KeywordCheckRequest;
import cc.wdev.platform.system.site.domain.request.KeywordRequest;
import cc.wdev.platform.system.site.domain.vo.KeywordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "KeywordAdminController", description = "关键字控制器")
public class KeywordSysController {

    private final KeywordApi keywordApi;

    @PreAuthorize("hasAnyAuthority('system:keyword')")
    @Operation(summary = "获取关键字列表")
    @ApiResponse(description = "获取关键字列表")
    @PostMapping(API_V1_SYS_PREFIX + "/keyword/list")
    public R<Page<KeywordVo>> List(@Parameter(description = "关键字列表查询请求") @Valid KeywordRequest request) {
        return R.success(keywordApi.findKeywordList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:keyword')")
    @Operation(summary = "保存关键字")
    @ApiResponse(description = "保存关键字")
    @OperationLog("保存关键字")
    @PostMapping(API_V1_SYS_PREFIX + "/keyword/save")
    public R<?> save(@Parameter(description = "关键字保存表单") @RequestBody KeywordForm coinInvestorForm) {
        keywordApi.saveKeyword(coinInvestorForm);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:keyword')")
    @Operation(summary = "获取关键字详情")
    @ApiResponse(description = "获取关键字详情")
    @PostMapping(API_V1_SYS_PREFIX + "/keyword/details")
    public R<KeywordVo> details(@RequestParam("id") @Parameter(description = "关键字ID") Long id) {
        return R.success(keywordApi.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:keyword')")
    @Operation(summary = "删除关键字")
    @ApiResponse(description = "删除关键字")
    @OperationLog("删除关键字")
    @PostMapping(API_V1_SYS_PREFIX + "/keyword/delete")
    public R<?> delete(@Valid @Parameter(description = "删除关键字请求参数") DeleteRequest request) {
        return R.success(keywordApi.deleteKeyword(request));
    }

    @PreAuthorize("hasAnyAuthority('system:keyword')")
    @Operation(summary = "检查关键字是否重复")
    @ApiResponse(description = "检查关键字是否重复")
    @GetMapping(API_V1_SYS_PREFIX + "/keyword/check-keyword")
    public R<Boolean> checkTitle(@Parameter(description = "检查关键字重复请求参数") KeywordCheckRequest request) {
        return R.success(keywordApi.checkKeyword(request));
    }

}
