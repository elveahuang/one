package cc.wdev.platform.system.catalog.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.catalog.api.CatalogApi;
import cc.wdev.platform.system.catalog.domain.request.CatalogDeleteRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogSaveRequest;
import cc.wdev.platform.system.catalog.domain.vo.CatalogVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * 分类管理控制器（平台端）
 *
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "CatalogPltController", description = "分类管理控制器")
public class CatalogPltController extends AbstractController {

    private final CatalogApi catalogApi;

    @PreAuthorize("hasAnyAuthority('platform:catalog')")
    @Operation(summary = "获取分类列表")
    @ApiResponse(description = "获取分类列表")
    @PostMapping(API_V1_SYS_PREFIX + "/catalog/list")
    public R<Page<CatalogVo>> list(@Valid CatalogRequest request) {
        return R.success(catalogApi.findCatalogList(request));
    }

    @PreAuthorize("hasAnyAuthority('platform:catalog')")
    @Operation(summary = "获取分类详情")
    @ApiResponse(description = "获取分类详情")
    @GetMapping(API_V1_SYS_PREFIX + "/catalog/details")
    public R<CatalogVo> details(@Parameter(description = "分类ID") @RequestParam("id") Long id) {
        return R.success(catalogApi.getCatalog(id));
    }

    @PreAuthorize("hasAnyAuthority('platform:catalog')")
    @Operation(summary = "获取顶层分类")
    @ApiResponse(description = "获取顶层分类")
    @GetMapping(API_V1_SYS_PREFIX + "/catalog/root")
    public R<CatalogVo> root(@Parameter(description = "业务类型") @RequestParam("bizType") String bizType,
                             @Parameter(description = "业务ID") @RequestParam(value = "bizId", defaultValue = "0") Long bizId) {
        return R.success(catalogApi.getRootCatalog(bizType, bizId));
    }

    @PreAuthorize("hasAnyAuthority('platform:catalog')")
    @OperationLog("保存分类")
    @Operation(summary = "保存分类")
    @ApiResponse(description = "保存分类")
    @PostMapping(API_V1_SYS_PREFIX + "/catalog/save")
    public R<CatalogVo> save(@RequestBody @Valid CatalogSaveRequest request) {
        return R.success(catalogApi.saveCatalog(request));
    }

    @PreAuthorize("hasAnyAuthority('platform:catalog')")
    @OperationLog("删除分类")
    @Operation(summary = "删除分类")
    @ApiResponse(description = "删除分类")
    @PostMapping(API_V1_SYS_PREFIX + "/catalog/delete")
    public R<?> delete(@RequestBody @Valid CatalogDeleteRequest request) {
        catalogApi.deleteCatalog(request);
        return R.success();
    }

}
