package cc.wdev.platform.system.ai.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.ai.api.AiApiKeyApi;
import cc.wdev.platform.system.ai.domain.request.AiApiKeyRequest;
import cc.wdev.platform.system.ai.domain.request.AiApiKeySearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeySimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeyVo;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

@RestController
@AllArgsConstructor
@Tag(name = "AiApiKeyPltController", description = "AI API Key Controller")
public class AiApiKeyPltController extends AbstractController {
    private final AiApiKeyApi appKeyApi;

    @PreAuthorize("hasAnyAuthority('platform:appkey')")
    @OperationLog("生成appKey密钥")
    @Operation(summary = "生成appKey密钥")
    @ApiResponse(description = "生成appKey密钥")
    @PostMapping(API_V1_SYS_PREFIX + "/appkey/generate")
    public R<AiApiKeySimpleVo> generate(@RequestBody AiApiKeyRequest appKeyGenerateRequest) {
        return R.success(appKeyApi.generate(appKeyGenerateRequest));
    }

    @PreAuthorize("hasAnyAuthority('platform:appkey')")
    @Operation(summary = "获取app密钥列表")
    @ApiResponse(description = "获取app密钥列表")
    @PostMapping(API_V1_SYS_PREFIX + "/appkey/list")
    public R<Page<AiApiKeyVo>> list(AiApiKeySearchRequest request) {
        return R.success(appKeyApi.findByPage(request));
    }

    @PreAuthorize("hasAnyAuthority('platform:appkey')")
    @OperationLog("删除app密钥")
    @Operation(summary = "删除app密钥")
    @ApiResponse(description = "删除app密钥")
    @PostMapping(API_V1_SYS_PREFIX + "/appkey/delete")
    public R<?> delete(@RequestBody DeleteRequest request) {
        appKeyApi.deleteApiKey(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:appkey')")
    @OperationLog("编辑app密钥描述")
    @Operation(summary = "编辑app密钥描述")
    @ApiResponse(description = "编辑app密钥描述")
    @PostMapping(API_V1_SYS_PREFIX + "/appkey/edit")
    public R<?> edit(@RequestBody AiApiKeyRequest request) {
        appKeyApi.edit(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('platform:appkey')")
    @Operation(summary = "查看appKey详情")
    @ApiResponse(description = "查看appKey详情")
    @PostMapping(API_V1_SYS_PREFIX + "/appkey/details")
    public R<AiApiKeyVo> details(@RequestParam Long id) {
        return R.success(appKeyApi.details(id));
    }

}
