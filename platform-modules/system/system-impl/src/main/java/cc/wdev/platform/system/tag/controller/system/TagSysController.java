package cc.wdev.platform.system.tag.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.tag.domain.entity.TagEntity;
import cc.wdev.platform.system.tag.domain.request.TagDeleteRequest;
import cc.wdev.platform.system.tag.domain.request.TagSaveRequest;
import cc.wdev.platform.system.tag.domain.request.TagSearchRequest;
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

/**
 * @author irving
 */
@RestController
@AllArgsConstructor
@Tag(name = "TagAdminController", description = "标签管理控制器")
public class TagSysController extends AbstractController {

    private final TagService tagService;

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "获取标签列表")
    @ApiResponse(description = "获取标签列表")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/list")
    public R<Page<TagEntity>> children(TagSearchRequest request) {
        request.setTenantId(0L);
        TagEntity example = TagEntity.builder().bizType(request.getBizType()).build();
        example.setActive(ActiveTypeEnum.ENABLED.getValue());
        return R.success(tagService.findByPage(request.getPageable(), example));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "查询标签页面")
    @ApiResponse(description = "查询标签页面")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/search")
    public R<Page<TagEntity>> tagSearch(TagSearchRequest request) {
        request.setTenantId(0L);
        return R.success(tagService.findByPage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "获取标签详情")
    @ApiResponse(description = "获取标签详情")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/details")
    public R<TagEntity> tagDetails(@Parameter(description = "标签ID") @RequestParam("id") Long id) {
        return R.success(tagService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "检查标签标题")
    @ApiResponse(description = "检查标签标题")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/check-title")
    public R<Boolean> checkTitle(@Parameter(description = "标签ID") @RequestParam(value = "id", required = false) Long id,
                                 @Parameter(description = "标签标题") @RequestParam("title") String title) {
        return R.success(tagService.checkTitle(id, title));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @OperationLog("保存标签")
    @Operation(summary = "保存标签")
    @ApiResponse(description = "保存标签")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/save")
    public R<?> tagSave(@RequestBody @Valid TagSaveRequest form) {
        form.setTenantId(TenantContext.getTenantId());
        tagService.saveTag(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @OperationLog("删除标签")
    @Operation(summary = "删除标签")
    @ApiResponse(description = "删除标签")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/delete")
    public R<?> delete(@Valid TagDeleteRequest request) {
        request.setTenantId(0L);
        tagService.deleteTag(request);
        return R.success();
    }

}
