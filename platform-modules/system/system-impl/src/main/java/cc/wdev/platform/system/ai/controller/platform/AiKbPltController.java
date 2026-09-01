package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiKbApi;
import cc.wdev.platform.system.ai.domain.request.*;
import cc.wdev.platform.system.ai.domain.vo.*;
import cc.wdev.platform.system.ai.enums.AiKbItemTypeEnum;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * 知识库管理控制器
 *
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "AiKbPltController", description = "知识库管理控制器")
public class AiKbPltController extends AbstractController {

    private final AiKbApi aiKbApi;

    // ------------------------------------------------------------------------------
    // 知识库
    // ------------------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("保存知识库")
    @Operation(summary = "保存知识库")
    @ApiResponse(description = "保存知识库")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/save")
    public R<?> save(@RequestBody @Valid AiKbSaveRequest request) {
        aiKbApi.saveKb(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "查询知识库详情")
    @ApiResponse(description = "查询知识库详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/details")
    public R<AiKbVo> details(@Parameter(description = "知识库ID") @RequestParam("id") Long id) {
        return R.success(aiKbApi.getKb(GetRequest.builder().id(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "获取知识库列表")
    @ApiResponse(description = "获取知识库列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/list")
    public R<Page<AiKbVo>> list(AiKbSearchRequest request) {
        return R.success(aiKbApi.findKbByPage(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("删除知识库")
    @Operation(summary = "删除知识库")
    @ApiResponse(description = "删除知识库")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/delete")
    public R<?> delete(@RequestBody @Valid DeleteRequest request) {
        aiKbApi.deleteKb(request);
        return R.success();
    }

    // ------------------------------------------------------------------------------
    // 知识库 - 知识条目
    // ------------------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("创建文本知识条目")
    @Operation(summary = "创建文本知识条目")
    @ApiResponse(description = "创建文本知识条目")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/createQaItem")
    public R<Void> createQaItem(@RequestBody @Valid AiKbItemSaveRequest request) {
        request.setType(AiKbItemTypeEnum.QA.getValue());
        this.aiKbApi.createItem(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("创建文本知识条目")
    @Operation(summary = "创建文本知识条目")
    @ApiResponse(description = "创建文本知识条目")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/createTextItem")
    public R<Void> createTextItem(@RequestBody @Valid AiKbItemSaveRequest request) {
        request.setType(AiKbItemTypeEnum.TEXT.getValue());
        this.aiKbApi.createItem(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "分页查询知识条目")
    @ApiResponse(description = "分页查询知识条目")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/item/list")
    public R<Page<AiKbItemVo>> itemList(AiKbItemSearchRequest request) {
        return R.success(aiKbApi.findItemByPage(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "查询知识条目详情")
    @ApiResponse(description = "查询知识条目详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/item/details")
    public R<AiKbItemVo> itemDetails(@Parameter(description = "知识条目ID") @RequestParam("id") Long id) {
        return R.success(aiKbApi.getKbItem(GetRequest.builder().id(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("更新知识条目")
    @Operation(summary = "更新知识条目")
    @ApiResponse(description = "更新知识条目")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/item/update")
    public R<Void> updateItem(@RequestBody @Valid AiKbItemSaveRequest request) {
        this.aiKbApi.updateItem(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("删除知识条目")
    @Operation(summary = "删除知识条目")
    @ApiResponse(description = "删除知识条目")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/item/delete")
    public R<Void> deleteItem(@RequestBody @Valid DeleteRequest request) {
        this.aiKbApi.deleteItem(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "查询条目分片")
    @ApiResponse(description = "查询条目分片")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/chunk/list")
    public R<List<AiKbChunkVo>> chunkList(@Parameter(description = "知识条目ID") @RequestParam("kbItemId") Long kbItemId) {
        return R.success(aiKbApi.findChunks(kbItemId));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "知识库统计")
    @ApiResponse(description = "知识库统计")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/stats")
    public R<AiKbStatsVo> stats(@Parameter(description = "知识库ID") @RequestParam("id") Long id) {
        return R.success(aiKbApi.getKbStats(id));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "分页查询向量化任务")
    @ApiResponse(description = "分页查询向量化任务")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/task/list")
    public R<Page<AiKbTaskVo>> taskList(AiKbTaskSearchRequest request) {
        return R.success(aiKbApi.findVectorTasks(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "查询向量化任务详情")
    @ApiResponse(description = "查询向量化任务详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/task/details")
    public R<AiKbTaskVo> taskDetails(@Parameter(description = "任务ID") @RequestParam("id") Long id) {
        return R.success(aiKbApi.getVectorTask(id));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("重试向量化任务")
    @Operation(summary = "重试向量化任务")
    @ApiResponse(description = "重试向量化任务")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/task/retry")
    public R<Void> retryTask(@Parameter(description = "任务ID") @RequestParam("id") Long id) {
        aiKbApi.retryTask(id);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "知识库检索评估")
    @ApiResponse(description = "知识库检索评估")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/eval")
    public R<List<AiKbEvalResultVo>> eval(@RequestBody @Valid AiKbEvalRequest request) {
        return R.success(aiKbApi.evalKb(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("上传文档类型知识条目")
    @Operation(summary = "上传文档类型知识条目")
    @ApiResponse(description = "上传文档类型知识条目")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/document/upload")
    public R<Void> createDocumentItem(@RequestBody @Valid AiKbItemSaveRequest request, MultipartHttpServletRequest servletRequest) throws IOException {
        // 获取待上传文件集合
        List<MultipartFile> files = Lists.newArrayList();
        Iterator<String> it = servletRequest.getFileNames();
        while (it.hasNext()) {
            files.add(servletRequest.getFile(it.next()));
        }
        // 处理知识库文件
        if (CollectionUtils.isNotEmpty(files)) {
            for (MultipartFile file : files) {
                aiKbApi.createDocumentItem(request, file.getResource());
            }
        }
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @OperationLog("重建知识库向量")
    @Operation(summary = "重建知识库向量")
    @ApiResponse(description = "重建知识库向量")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/kb/rebuild")
    public R<?> rebuild(@RequestBody @Valid GetRequest request) {
        aiKbApi.rebuildKb(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:kb')")
    @Operation(summary = "知识库语义检索")
    @ApiResponse(description = "知识库语义检索")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/kb/search")
    public R<List<AiKbSearchResultVo>> search(@Parameter(description = "知识库ID") @RequestParam("id") Long id,
                                              @Parameter(description = "查询内容") @RequestParam("query") String query,
                                              @Parameter(description = "返回条数") @RequestParam(value = "topK", required = false) Integer topK,
                                              @Parameter(description = "相似度阈值") @RequestParam(value = "similarityThreshold", required = false) Double similarityThreshold) {
        return R.success(aiKbApi.searchKb(id, query, topK, similarityThreshold));
    }

}
