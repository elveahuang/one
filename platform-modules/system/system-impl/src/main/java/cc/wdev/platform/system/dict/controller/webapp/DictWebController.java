package cc.wdev.platform.system.dict.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.dict.api.DictApi;
import cc.wdev.platform.system.dict.domain.request.DictSearchRequest;
import cc.wdev.platform.system.dict.domain.vo.DictTypeVo;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "DictAppController", description = "字典控制器")
public class DictWebController {

    protected DictApi dictApi;

    @Authenticated
    @Operation(summary = "获取字典类型")
    @ApiResponse(description = "获取字典类型")
    @PostMapping(API_V1_PREFIX + "/dict/type")
    public R<DictTypeVo> type(BizTypeRequest request) {
        return R.success(dictApi.getDictType(request));
    }

    @Authenticated
    @Operation(summary = "分页搜索字典")
    @ApiResponse(description = "分页搜索字典")
    @PostMapping(API_V1_PREFIX + "/dict/search")
    public R<Page<DictVo>> search(@Parameter(description = "字典搜索参数") DictSearchRequest request) {
        return R.success(dictApi.search(request));
    }

    @Authenticated
    @Operation(summary = "字典列表")
    @ApiResponse(description = "字典列表")
    @PostMapping(API_V1_PREFIX + "/dict/filter")
    public R<List<DictVo>> filter(@Parameter(description = "字典搜索参数") DictSearchRequest request) {
        return R.success(dictApi.list(request));
    }

    @Authenticated
    @Operation(summary = "获取关联字典")
    @ApiResponse(description = "获取关联字典")
    @GetMapping(API_V1_PREFIX + "/dict/get")
    public R<RelationVo<DictVo>> getRelation(@Parameter(description = "关联查询参数") RelationRequest request) {
        return R.success(dictApi.getRelation(request));
    }

    @Authenticated
    @Operation(summary = "保存字典关联")
    @ApiResponse(description = "保存字典关联")
    @PostMapping(API_V1_PREFIX + "/dict/save-relation")
    @OperationLog("保存字典关联")
    public R<?> saveRelation(@Parameter(description = "关联保存参数") RelationSaveRequest request) {
        dictApi.saveRelation(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "删除字典关联")
    @ApiResponse(description = "删除字典关联")
    @PostMapping(API_V1_PREFIX + "/dict/delete-relation")
    @OperationLog("删除字典关联")
    public R<?> deleteRelation(@Parameter(description = "关联删除参数") RelationRequest request) {
        dictApi.deleteRelation(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "保存字典个性化排序")
    @ApiResponse(description = "保存字典个性化排序")
    @PostMapping(API_V1_PREFIX + "/dict/save-sequence")
    @OperationLog("保存字典个性化排序")
    public R<?> saveSequence(@RequestBody @Parameter(description = "排序参数") SequenceRequest request) {
        dictApi.saveSequence(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "获取字典个性化排序")
    @ApiResponse(description = "获取字典个性化排序")
    @GetMapping(API_V1_PREFIX + "/dict/get-sequence")
    public R<SequenceVo> getSequence(SequenceRequest request) {
        return R.success(dictApi.getSequence(request));
    }
}
