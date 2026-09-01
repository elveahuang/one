package cc.wdev.platform.system.log.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.log.api.SearchApi;
import cc.wdev.platform.system.log.domain.request.SearchLogRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSaveRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSearchRequest;
import cc.wdev.platform.system.log.domain.vo.SearchLogVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "SearchLogController", description = "搜索日志控制器")
public class SearchLogWebController extends AbstractController {

    private final SearchApi searchApi;

    @Authenticated
    @Operation(summary = "查询个人搜索历史记录")
    @ApiResponse(description = "查询个人搜索历史记录")
    @PostMapping(API_V1_PREFIX + "/search-log/history")
    public R<List<SearchLogVo>> getSearchLogHistoryList(@Parameter(description = "搜索个人历史记录查询参数") SearchLogSearchRequest request) {
        return R.success(this.searchApi.getMySearchLog(request));
    }

    @Authenticated
    @Operation(summary = "保存个人搜索历史记录")
    @ApiResponse(description = "保存个人搜索历史记录")
    @OperationLog("保存个人搜索历史记录")
    @PostMapping(API_V1_PREFIX + "/search-log/save")
    public R<?> saveSearchLogHistoryList(@RequestBody @Valid SearchLogSaveRequest request) {
        searchApi.saveSearchLog(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "查询搜索热榜")
    @ApiResponse(description = "查询搜索热榜")
    @PostMapping(API_V1_PREFIX + "/search-log/hot")
    public R<List<SearchLogVo>> getSearchLogHotList(@Parameter(description = "搜索热榜查询参数") SearchLogSearchRequest request) {
        return R.success(this.searchApi.getHotSearchLog(request));
    }

    @Authenticated
    @Operation(summary = "删除个人搜索历史记录")
    @ApiResponse(description = "删除个人搜索历史记录")
    @OperationLog("删除个人搜索历史记录")
    @PostMapping(API_V1_PREFIX + "/search-log/delete")
    public R<?> deleteSearchLogHistory(@RequestBody @Valid SearchLogRequest request) {
        searchApi.deleteMySearchLog(request);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "删除个人搜索历史记录")
    @ApiResponse(description = "删除个人搜索历史记录")
    @OperationLog("删除个人搜索历史记录")
    @PostMapping(API_V1_PREFIX + "/search-log/deleteAll")
    public R<?> deleteAllSearchLogHistory() {
        searchApi.deleteMySearchLog();
        return R.success();
    }

}
