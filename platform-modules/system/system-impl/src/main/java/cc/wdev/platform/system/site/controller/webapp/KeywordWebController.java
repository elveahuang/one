package cc.wdev.platform.system.site.controller.webapp;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.site.api.KeywordApi;
import cc.wdev.platform.system.site.domain.request.KeywordRequest;
import cc.wdev.platform.system.site.domain.vo.KeywordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_WEB_PREFIX;


/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "KeywordAppController", description = "关键字控制器")
public class KeywordWebController extends AbstractController {

    private final KeywordApi keywordApi;

    @Anonymous
    @Operation(summary = "获取关键字列表")
    @ApiResponse(description = "获取关键字列表")
    @PostMapping(API_V1_WEB_PREFIX + "/keyword/list")
    public R<Page<KeywordVo>> list(@Parameter(description = "关键字查询请求") KeywordRequest request) {
        return R.success(keywordApi.findKeywordList(request));
    }

    @Anonymous
    @Operation(summary = "获取关键字详情")
    @ApiResponse(description = "获取关键字详情")
    @PostMapping(API_V1_WEB_PREFIX + "/keyword/details")
    public R<KeywordVo> details(@Parameter(description = "关键字ID") @RequestParam("id") Long id) {
        KeywordVo entity = keywordApi.findById(id);
        return R.success(entity);
    }
}
