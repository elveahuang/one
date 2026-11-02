package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.DeleteRequest;
import cc.wdev.platform.commons.domain.GetRequest;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiMcpApi;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "AiMcpAdminController", description = "MCP管理控制器")
public class AiMcpPltController extends AbstractController {

    private final AiMcpApi aiMcpApi;

    @PreAuthorize("hasAnyAuthority('dev:ai:config:mcp')")
    @OperationLog("保存MCP")
    @Operation(summary = "保存MCP")
    @ApiResponse(description = "保存MCP")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/mcp/save")
    public R<?> save(@RequestBody @Valid AiMcpServerSaveRequest request) {
        aiMcpApi.saveAiMcpServer(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:mcp')")
    @Operation(summary = "查询MCP详情")
    @ApiResponse(description = "查询MCP详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/mcp/details")
    public R<AiMcpServerVo> details(@Parameter(description = "MCPID") @RequestParam("id") Long id) {
        return R.success(aiMcpApi.getAiMcpServer(GetRequest.of(id)));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:mcp')")
    @Operation(summary = "获取MCP列表")
    @ApiResponse(description = "获取MCP列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/mcp/list")
    public R<Page<AiMcpServerVo>> list(AiMcpServerSearchRequest request) {
        return R.success(aiMcpApi.findAiMcpServers(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:mcp')")
    @OperationLog("删除MCP")
    @Operation(summary = "删除MCP")
    @ApiResponse(description = "删除MCP")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/mcp/delete")
    public R<?> delete(@RequestBody @Valid DeleteRequest request) {
        aiMcpApi.deleteAiMcpServer(request);
        return R.success();
    }

}
