package cc.wdev.platform.system.tag.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.tag.api.TagApi;
import cc.wdev.platform.system.tag.domain.request.*;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import cc.wdev.platform.system.tag.service.TagService;
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

@RestController
@AllArgsConstructor
@Tag(name = "TenantTagAdminController", description = "租户标签管理控制器")
public class TenantTagSysController extends AbstractController {

    private final TagService tagService;

    private final TagApi tagApi;

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "查询租户标签页面")
    @ApiResponse(description = "查询租户标签页面")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/tag/search")
    public R<Page<TagVo>> tagSearch(TagSearchRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        return R.success(tagService.search(request));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "获取租户标签详情")
    @ApiResponse(description = "获取租户标签详情")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/tag/details")
    public R<TagVo> tagDetails(@Parameter(description = "标签ID") @RequestParam("id") Long id) {
        TagVo vo = tagService.getTag(TagRequest.builder()
            .tagId(id)
            .build());
        return R.success(vo);
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "检查租户标签标题")
    @ApiResponse(description = "检查租户标签标题")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/tag/check-title")
    public R<Boolean> checkTitle(@RequestBody @Valid TagTitleCheckRequest request) {
        // 检查顶层租户是否有标签重复
        request.setTid(0L);
        Boolean allowInd = tagService.checkTitle(request);
        if (allowInd) {
            request.setTid(TenantContext.getTenantId());
            return R.success(tagService.checkTitle(request));
        }
        return R.success(Boolean.FALSE);
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @OperationLog("保存标签")
    @Operation(summary = "保存标签")
    @ApiResponse(description = "保存标签")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/tag/save")
    public R<?> tagSave(@RequestBody @Valid TagSaveRequest form) {
        form.setTenantId(TenantContext.getTenantId());
        tagService.saveTag(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @OperationLog("删除标签")
    @Operation(summary = "删除标签")
    @ApiResponse(description = "删除标签")
    @PostMapping(API_V1_SYS_PREFIX + "/tenant/tag/delete")
    public R<?> delete(@RequestBody @Valid TagDeleteRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        tagApi.deleteTag(request);
        return R.success();
    }

}
