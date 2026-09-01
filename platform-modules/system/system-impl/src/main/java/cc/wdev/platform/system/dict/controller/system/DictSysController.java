package cc.wdev.platform.system.dict.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.dict.domain.entity.DictEntity;
import cc.wdev.platform.system.dict.domain.request.DictDeleteRequest;
import cc.wdev.platform.system.dict.domain.request.DictSaveRequest;
import cc.wdev.platform.system.dict.domain.request.DictSearchRequest;
import cc.wdev.platform.system.dict.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "DictAdminController", description = "字典后台管理控制器")
public class DictSysController {

    protected DictService dictService;

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "检查字典项编号是否可用")
    @ApiResponse(description = "检查字典项编号是否可用")
    @GetMapping(API_V1_SYS_PREFIX + "/dict/item/check")
    public R<Boolean> checkItemCode(@Parameter(description = "字典子项编号") @RequestParam("code") String code,
                                    @Parameter(description = "字典子项ID") @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(!dictService.existsByCode(code, id));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "获取字典子项列表")
    @ApiResponse(description = "获取字典子项列表")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/item/list")
    public R<Page<DictEntity>> children(@Parameter(description = "查询字典子项请求参数") DictSearchRequest request) {
        request.setTenantId(0L);
        String bizType = StringUtils.nvl(request.getBizType(), "").trim();
        DictEntity example = DictEntity.builder().bizType(bizType).build();
        example.setActive(ActiveTypeEnum.ENABLED.getValue());
        return R.success(dictService.findByPage(request.getPageable(), example));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "查询字典子项")
    @ApiResponse(description = "查询字典子项")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/item/search")
    public R<Page<DictEntity>> dictSearch(@Parameter(description = "查询字典子项请求参数") DictSearchRequest request) {
        request.setTenantId(0L);
        return R.success(dictService.findByPage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "获取字典详情")
    @ApiResponse(description = "获取字典详情")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/item/details")
    public R<DictEntity> itemDetails(@Parameter(description = "字典子项ID") @RequestParam("id") Long id) {
        return R.success(dictService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "保存字典子项")
    @ApiResponse(description = "保存字典子项")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/item/save")
    public R<?> save(@Parameter(description = "保存字典子项请求参数") @Valid DictSaveRequest request) {
        request.setTenantId(0L);
        dictService.saveDict(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @OperationLog("删除字典子项")
    @Operation(summary = "删除字典子项")
    @ApiResponse(description = "删除字典子项")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/item/delete")
    public R<?> delete(@Parameter(description = "删除字典子项请求参数") @Valid DictDeleteRequest request) {
        request.setTenantId(0L);
        dictService.deleteDict(request);
        return R.success();
    }

}
