package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.ai.enums.AiModelProvider;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiModelApi;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiModelOptionsVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.service.AiModelService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "AiModelAdminController", description = "模型管理控制器")
public class AiModelPltController extends AbstractController {

    private final AiModelApi aiModelApi;

    private final AiModelService aiModelService;

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @OperationLog("初始模型")
    @Operation(summary = "初始模型")
    @ApiResponse(description = "初始模型")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/model/initialize")
    public R<Void> save() {
        aiModelApi.initialize();
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @OperationLog("保存模型")
    @Operation(summary = "保存模型")
    @ApiResponse(description = "保存模型")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/model/save")
    public R<?> save(@RequestBody @Valid AiModelSaveRequest request) {
        aiModelApi.saveAiModel(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @Operation(summary = "查询模型详情")
    @ApiResponse(description = "查询模型详情")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/model/details")
    public R<AiModelVo> details(@Parameter(description = "模型ID") @RequestParam("id") Long id) {
        return R.success(aiModelApi.getAiModel(AiModelGetRequest.builder().id(id).build()));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @Operation(summary = "获取模型列表")
    @ApiResponse(description = "获取模型列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/model/list")
    public R<Page<AiModelVo>> list(AiModelSearchRequest request) {
        return R.success(aiModelApi.findAiModels(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @OperationLog("删除模型")
    @Operation(summary = "删除模型")
    @ApiResponse(description = "删除模型")
    @PostMapping(API_V1_SYS_PREFIX + "/ai/model/delete")
    public R<?> delete(@RequestBody @Valid DeleteRequest request) {
        aiModelApi.deleteAiModel(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @Operation(summary = "检查模型编号是否存在")
    @ApiResponse(description = "检查模型编号是否存在")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/model/check")
    public R<Boolean> check(@RequestParam("modelCode") String modelCode,
                            @RequestParam(value = "id", required = false) Long id) {
        return R.success(aiModelService.existsByCode(modelCode, id));
    }

    @PreAuthorize("hasAnyAuthority('dev:ai:config:model')")
    @Operation(summary = "模型类型和供应商列表")
    @ApiResponse(description = "模型类型和供应商列表")
    @GetMapping(API_V1_SYS_PREFIX + "/ai/model/options")
    public R<AiModelOptionsVo> options() {
        List<SimpleOptionVo> modelProviders = Arrays.stream(AiModelProvider.values()).map(s -> SimpleOptionVo.builder()
            .title(s.getValue())
            .value(s.getValue())
            .label(s.getDescription())
            .labelKey(s.getLabelKey())
            .labelGroup(s.getLabelGroup())
            .build()
        ).toList();

        List<SimpleOptionVo> serviceProviders = Arrays.stream(AiServiceProvider.values()).map(s -> SimpleOptionVo.builder()
            .title(s.getValue())
            .value(s.getValue())
            .label(s.getDescription())
            .labelKey(s.getLabelKey())
            .labelGroup(s.getLabelGroup())
            .build()
        ).toList();

        List<SimpleOptionVo> modelTypes = Arrays.stream(AiModelType.values()).map(s -> SimpleOptionVo.builder()
            .title(s.getValue())
            .value(s.getValue())
            .label(s.getDescription())
            .labelKey(s.getLabelKey())
            .labelGroup(s.getLabelGroup())
            .build()
        ).toList();

        return R.success(AiModelOptionsVo.builder()
            .modelServiceProviders(serviceProviders)
            .modelProviders(modelProviders)
            .modelTypes(modelTypes)
            .build()
        );
    }

}
