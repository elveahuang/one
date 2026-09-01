package cc.wdev.platform.system.dict.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.dict.domain.request.*;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import cc.wdev.platform.system.dict.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "TenantDictAdminController", description = "租户字典后台管理控制器")
public class TenantDictSysController {

    protected DictService dictService;

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "检查字典项编号是否可用")
    @ApiResponse(description = "检查字典项编号是否可用")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/dict/check")
    public R<Boolean> checkCode(@RequestBody @Valid DictCodeCheckRequest request) {
        // 检查顶层租户是否有标签重复
        request.setTid(0L);
        Boolean allowInd = dictService.checkCode(request);
        if (allowInd) {
            request.setTid(TenantContext.getTenantId());
            return R.success(dictService.checkCode(request));
        }
        return R.success(Boolean.FALSE);
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "检查租户字典项标题")
    @ApiResponse(description = "检查租户字典项标题")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/dict/check-title")
    public R<Boolean> checkTitle(@RequestBody @Valid DictTitleCheckRequest request) {
        // 检查顶层租户是否有标签重复
        request.setTid(0L);
        Boolean allowInd = dictService.checkTitle(request);
        if (allowInd) {
            request.setTid(TenantContext.getTenantId());
            return R.success(dictService.checkTitle(request));
        }
        return R.success(Boolean.FALSE);
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "查询字典子项")
    @ApiResponse(description = "查询字典子项")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/dict/search")
    public R<Page<DictVo>> search(@Parameter(description = "查询字典子项请求参数") DictSearchRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        return R.success(dictService.search(request));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "获取字典详情")
    @ApiResponse(description = "获取字典详情")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/dict/details")
    public R<DictVo> details(@Parameter(description = "字典子项ID") @RequestParam("id") Long id) {
        return R.success(dictService.getDict(DictRequest.builder().dictId(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @OperationLog("保存字典子项")
    @Operation(summary = "保存字典子项")
    @ApiResponse(description = "保存字典子项")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/dict/save")
    public R<?> save(@RequestBody @Parameter(description = "保存字典子项请求参数") @Valid DictSaveRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        dictService.saveDict(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @OperationLog("删除字典子项")
    @Operation(summary = "删除字典子项")
    @ApiResponse(description = "删除字典子项")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/dict/delete")
    public R<?> delete(@RequestBody @Parameter(description = "删除字典子项请求参数") @Valid DictDeleteRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        dictService.deleteDict(request);
        return R.success();
    }

}
