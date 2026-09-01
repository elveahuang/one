package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiAgentApi;
import cc.wdev.platform.system.ai.api.AiModelApi;
import cc.wdev.platform.system.ai.api.AiToolApi;
import cc.wdev.platform.system.ai.domain.request.AiAgentGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentParamVo;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.ai.service.AiAgentService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
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
@Tag(name = "AiAgentAdminController", description = "智能体管理控制器")
public class AiAgentPltController extends AbstractController {

    private final AiAgentApi aiAgentApi;

    private final AiModelApi aiModelApi;

    private final AiToolApi aiToolApi;

    private final AiAgentService aiAgentService;

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @OperationLog("初始智能体")
    @Operation(summary = "初始智能体")
    @ApiResponse(description = "初始智能体")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/agent/initialize")
    public R<Void> save() {
        aiAgentApi.initialize();
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @OperationLog("保存智能体")
    @Operation(summary = "保存智能体")
    @ApiResponse(description = "保存智能体")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/agent/save")
    public R<Void> save(@RequestBody @Valid AiAgentSaveRequest request) {
        aiAgentApi.saveAiAgent(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @Operation(summary = "查询智能体详情")
    @ApiResponse(description = "查询智能体详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/agent/details")
    public R<AiAgentVo> details(@Parameter(description = "智能体ID") @RequestParam("id") Long id) {
        return R.success(aiAgentApi.getAiAgent(AiAgentGetRequest.builder().id(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @Operation(summary = "获取智能体列表")
    @ApiResponse(description = "获取智能体列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/agent/list")
    public R<Page<AiAgentVo>> list(AiAgentSearchRequest request) {
        return R.success(aiAgentApi.findAiAgents(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @OperationLog("删除智能体")
    @Operation(summary = "删除智能体")
    @ApiResponse(description = "删除智能体")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/agent/delete")
    public R<Void> delete(@RequestBody @Valid DeleteRequest request) {
        aiAgentApi.deleteAiAgent(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @Operation(summary = "获取智能体所需的模型和工具列表")
    @ApiResponse(description = "获取智能体所需的模型和工具列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/agent/params")
    public R<AiAgentParamVo> params() {
        return R.success(new AiAgentParamVo(aiModelApi.getModels(), aiToolApi.getTools()));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:agent')")
    @Operation(summary = "检查智能体编号是否存在")
    @ApiResponse(description = "检查智能体编号是否存在")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/agent/check")
    public R<Boolean> checkModelCode(@RequestParam("agentCode") String agentCode,
                                     @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return R.success(aiAgentService.existsByCode(agentCode, id));
    }

}
