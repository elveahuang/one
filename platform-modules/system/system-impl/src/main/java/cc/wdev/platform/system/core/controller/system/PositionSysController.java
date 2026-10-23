package cc.wdev.platform.system.core.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.IdsRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.core.api.PositionApi;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.request.PositionSearchRequest;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import cc.wdev.platform.system.core.service.PositionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "PositionAdminController", description = "岗位后台控制器")
public class PositionSysController extends AbstractController {

    private final PositionApi positionApi;

    private final PositionService positionService;

    @PreAuthorize("hasAnyAuthority('system:position')")
    @Operation(summary = "获取岗位树")
    @ApiResponse(description = "获取岗位树")
    @PostMapping(API_V1_SYS_PREFIX + "/position/tree")
    public R<List<PositionVo>> tree() {
        return R.success(positionApi.findPositionTree());
    }

    @PreAuthorize("hasAnyAuthority('system:position')")
    @Operation(summary = "获取岗位列表")
    @ApiResponse(description = "获取岗位列表")
    @PostMapping(API_V1_SYS_PREFIX + "/position/list")
    public R<Page<PositionVo>> list(@Parameter(description = "岗位查询请求") @Valid PositionSearchRequest request) {
        return R.success(positionApi.findPositionPage(request));
    }

    @PreAuthorize("hasAnyAuthority('system:position')")
    @Operation(summary = "获取岗位详情")
    @ApiResponse(description = "获取岗位详情")
    @PostMapping(API_V1_SYS_PREFIX + "/position/details")
    public R<PositionVo> details(@Parameter(description = "岗位ID") @RequestParam("id") Long id) {
        return R.success(positionApi.findPositionDetails(id));
    }

    @PreAuthorize("hasAnyAuthority('system:position')")
    @Operation(summary = "保存岗位")
    @ApiResponse(description = "保存岗位")
    @PostMapping(API_V1_SYS_PREFIX + "/position/save")
    @OperationLog("保存岗位")
    public R<?> save(@Parameter(description = "岗位保存请求") @Valid @RequestBody PositionSaveDto saveDto) {
        return R.success(positionApi.savePosition(saveDto));
    }

    @PreAuthorize("hasAnyAuthority('system:position')")
    @Operation(summary = "删除岗位")
    @ApiResponse(description = "删除岗位")
    @PostMapping(API_V1_SYS_PREFIX + "/position/delete")
    @OperationLog("删除岗位")
    public R<?> delete(@Parameter(description = "岗位ID数组") @Valid IdsRequest request) {
        positionApi.deletePosition(request.getIds());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:position')")
    @Operation(summary = "检查编号是否可用")
    @ApiResponse(description = "检查编号是否可用")
    @GetMapping(API_V1_SYS_PREFIX + "/position/check")
    public R<Boolean> checkCode(@Parameter(description = "岗位编号") @RequestParam("code") String code,
                                @Parameter(description = "岗位ID") @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(!positionService.existsByCode(code, id));
    }

}
