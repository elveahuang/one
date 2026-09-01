package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiToolApi;
import cc.wdev.platform.system.ai.domain.request.AiToolGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiToolVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "AiToolAdminController", description = "工具管理控制器")
public class AiToolPltController extends AbstractController {

    private final AiToolApi aiToolApi;

    @PreAuthorize("hasAnyAuthority('dev:ai:config:tool')")
    @OperationLog("初始工具")
    @Operation(summary = "初始工具")
    @ApiResponse(description = "初始工具")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/tool/initialize")
    public R<Void> save() {
        aiToolApi.initialize();
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:tool')")
    @OperationLog("保存Tool")
    @Operation(summary = "保存Tool")
    @ApiResponse(description = "保存Tool")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/tool/save")
    public R<?> save(@RequestBody @Valid AiToolSaveRequest request) {
        aiToolApi.saveAiTool(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:tool')")
    @Operation(summary = "查询Tool详情")
    @ApiResponse(description = "查询Tool详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/tool/details")
    public R<AiToolVo> details(@Parameter(description = "ToolID") @RequestParam("id") Long id) {
        return R.success(aiToolApi.getAiTool(AiToolGetRequest.builder().id(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:tool')")
    @Operation(summary = "获取Tool列表")
    @ApiResponse(description = "获取Tool列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/tool/list")
    public R<Page<AiToolVo>> list(AiToolSearchRequest request) {
        return R.success(aiToolApi.findAiToolsPage(request));
    }

}
