package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.domain.entity.AiUsageEntity;
import cc.wdev.platform.system.ai.domain.request.AiUsageSearchRequest;
import cc.wdev.platform.system.ai.service.AiUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * AI 用量统计控制器
 *
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "AiUsagePltController", description = "AI 用量统计控制器")
public class AiUsagePltController extends AbstractController {

    private final AiUsageService aiUsageService;

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "分页查询 AI 用量")
    @ApiResponse(description = "分页查询 AI 用量")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/usage/list")
    public R<Page<AiUsageEntity>> list(AiUsageSearchRequest request) {
        return R.success(aiUsageService.findByPage(request));
    }

}
