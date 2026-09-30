package cc.wdev.platform.system.ai.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiKbApi;
import cc.wdev.platform.system.ai.domain.vo.AiKbSearchResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * 用户端知识库控制器
 *
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "AiKbWebController", description = "用户端知识库控制器")
public class AiKbWebController extends AbstractController {

    private final AiKbApi aiKbApi;

    @Authenticated
    @Operation(summary = "知识库语义检索")
    @ApiResponse(description = "知识库语义检索")
    @GetMapping(API_V1_PREFIX + "/ai/kb/search")
    public R<List<AiKbSearchResultVo>> search(
        @Parameter(description = "知识库ID") @RequestParam("id") Long id,
        @Parameter(description = "查询内容") @RequestParam("query") String query,
        @Parameter(description = "返回条数") @RequestParam(value = "topK", required = false) Integer topK,
        @Parameter(description = "相似度阈值") @RequestParam(value = "similarityThreshold", required = false) Double similarityThreshold) {
        return R.success(aiKbApi.searchKb(id, query, topK, similarityThreshold));
    }

}
