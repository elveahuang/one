package cc.wdev.platform.system.dict.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.request.BizTypeDeleteRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSaveRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSearchRequest;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "DictTypeAdminController", description = "字典类型管理控制器")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "成功"),
    @ApiResponse(responseCode = "400", description = "请求参数错误")
})
public class DictTypeSysController {

    private final BizTypeApi bizTypeApi;

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "查询标签列表")
    @ApiResponse(description = "查询标签列表")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/type/search")
    public R<Page<BizTypeVo<Object>>> dictTypeSearch(@Parameter(description = "查询标签列表请求参数") BizTypeSearchRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.DICT_TYPE.getValue());
        return R.success(this.bizTypeApi.findBizTypePage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "获取标签列表")
    @ApiResponse(description = "获取标签列表")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/type/list")
    public R<List<BizTypeVo<Object>>> dictTypeList(@Parameter(description = "获取标签列表请求参数") BizTypeSearchRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.DICT_TYPE.getValue());
        return R.success(this.bizTypeApi.findBizTypeList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @Operation(summary = "获取标签详情")
    @ApiResponse(description = "获取字典类型详情")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/type/details")
    public R<BizTypeVo<Object>> dictTypeDetail(BizTypeSearchRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.DICT_TYPE.getValue());
        return R.success(this.bizTypeApi.getBizType(request.getBizGroupType(), request.getBizType()));
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @OperationLog("删除标签类型")
    @Operation(summary = "删除标签类型")
    @ApiResponse(description = "删除标签类型")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/type/delete")
    public R<?> dictTypeDelete(@Valid @Parameter(description = "删除标签类型请求参数") BizTypeDeleteRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.DICT_TYPE.getValue());
        this.bizTypeApi.deleteBizType(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:dict')")
    @OperationLog("保存字典类型")
    @Operation(summary = "保存字典类型")
    @ApiResponse(description = "保存字典类型")
    @PostMapping(API_V1_SYS_PREFIX + "/dict/type/save")
    public R<?> dictTypeSave(@Valid @Parameter(description = "保存标签类型请求参数") BizTypeSaveRequest<Object> request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.DICT_TYPE.getValue());
        this.bizTypeApi.saveBizType(request);
        return R.success();
    }

}
