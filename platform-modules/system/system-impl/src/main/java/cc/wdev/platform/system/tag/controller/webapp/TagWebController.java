package cc.wdev.platform.system.tag.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.tag.api.TagApi;
import cc.wdev.platform.system.tag.domain.request.TagSearchRequest;
import cc.wdev.platform.system.tag.domain.vo.TagTypeVo;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author irving
 */
@RestController
@AllArgsConstructor
@Tag(name = "TagController", description = "标签控制器")
public class TagWebController extends AbstractController {

    private final TagApi tagApi;

    @Authenticated
    @Operation(summary = "获取标签类型")
    @ApiResponse(description = "获取标签类型")
    @PostMapping(API_V1_PREFIX + "/tag/type")
    public R<TagTypeVo> type(BizTypeRequest request) {
        return R.success(tagApi.getTagType(request));
    }

    @Authenticated
    @Operation(summary = "分页搜索标签")
    @ApiResponse(description = "分页搜索标签")
    @PostMapping(API_V1_PREFIX + "/tag/search")
    public R<Page<TagVo>> search(TagSearchRequest request) {
        return R.success(tagApi.search(request));
    }

    @Authenticated
    @Operation(summary = "标签列表")
    @ApiResponse(description = "标签列表")
    @PostMapping(API_V1_PREFIX + "/tag/filter")
    public R<List<TagVo>> filter(@Parameter(description = "标签搜索参数") TagSearchRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        return R.success(tagApi.list(request));
    }

    @Authenticated
    @Operation(summary = "获取关联标签")
    @ApiResponse(description = "获取关联标签")
    @GetMapping(API_V1_PREFIX + "/tag/get")
    public R<RelationVo<TagVo>> get(RelationRequest request) {
        return R.success(tagApi.getRelation(request));
    }

    @Authenticated
    @Operation(summary = "保存标签关联")
    @ApiResponse(description = "保存标签关联")
    @PostMapping(API_V1_PREFIX + "/tag/save-relation")
    @OperationLog("保存标签关联")
    public R<?> saveRelation(@RequestBody @Valid RelationSaveRequest request) {
        tagApi.saveRelation(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "删除标签关联")
    @ApiResponse(description = "删除标签关联")
    @PostMapping(API_V1_PREFIX + "/tag/delete-relation")
    @OperationLog("删除标签关联")
    public R<?> deleteRelation(RelationRequest request) {
        tagApi.deleteRelation(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "保存标签个性化排序")
    @ApiResponse(description = "保存标签个性化排序")
    @PostMapping(API_V1_PREFIX + "/tag/save-sequence")
    @OperationLog("保存标签个性化排序")
    public R<?> saveSequence(@RequestBody @Valid SequenceRequest request) {
        tagApi.saveSequence(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "获取标签个性化排序")
    @ApiResponse(description = "获取标签个性化排序")
    @GetMapping(API_V1_PREFIX + "/tag/get-sequence")
    public R<SequenceVo> getSequence(SequenceRequest request) {
        return R.success(tagApi.getSequence(request));
    }

}
