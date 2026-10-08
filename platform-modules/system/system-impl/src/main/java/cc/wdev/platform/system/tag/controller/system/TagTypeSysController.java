package cc.wdev.platform.system.tag.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.request.BizTypeDeleteRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSaveRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSearchRequest;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RestController
@AllArgsConstructor
@Tag(name = "TagTypeAdminController", description = "标签类型管理控制器")
public class TagTypeSysController extends AbstractController {

    private final BizTypeApi bizTypeApi;

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "查询标签列表")
    @ApiResponse(description = "查询标签列表")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/type/search")
    public R<Page<BizTypeVo<Object>>> tagTypeSearch(@Parameter(description = "标签查询请求") BizTypeSearchRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.TAG_TYPE.getValue());
        return R.success(this.bizTypeApi.findBizTypePage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "获取标签列表")
    @ApiResponse(description = "获取标签列表")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/type/list")
    public R<List<BizTypeVo<Object>>> tagTypeList(@Parameter(description = "标签查询请求") BizTypeSearchRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.TAG_TYPE.getValue());
        return R.success(this.bizTypeApi.findBizTypeList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @Operation(summary = "获取标签详情")
    @ApiResponse(description = "获取标签详情")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/type/details")
    public R<BizTypeVo<Object>> tagTypeDetail(@Parameter(description = "标签查询请求") BizTypeSearchRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.TAG_TYPE.getValue());
        return R.success(this.bizTypeApi.getBizType(request.getBizGroupType(), request.getBizType()));
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @OperationLog("删除标签类型")
    @Operation(summary = "删除标签类型")
    @ApiResponse(description = "删除标签类型")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/type/delete")
    public R<?> tagTypeDelete(@Parameter(description = "标签删除请求") @Valid BizTypeDeleteRequest request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.TAG_TYPE.getValue());
        this.bizTypeApi.deleteBizType(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:tag')")
    @OperationLog("保存标签类型")
    @Operation(summary = "保存标签类型")
    @ApiResponse(description = "保存标签类型")
    @PostMapping(API_V1_SYS_PREFIX + "/tag/type/save")
    public R<?> tagTypeSave(@Parameter(description = "标签保存请求") @Valid BizTypeSaveRequest<Object> request) {
        request.setBizGroupType(CoreBizGroupTypeEnum.TAG_TYPE.getValue());
        this.bizTypeApi.saveBizType(request);
        return R.success();
    }

}
