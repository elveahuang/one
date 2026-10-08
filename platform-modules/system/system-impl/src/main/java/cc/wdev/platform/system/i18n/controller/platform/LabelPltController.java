package cc.wdev.platform.system.i18n.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.i18n.domain.request.LabelEditRequest;
import cc.wdev.platform.system.i18n.domain.request.LabelSearchRequest;
import cc.wdev.platform.system.i18n.domain.vo.LabelVo;
import cc.wdev.platform.system.i18n.service.LabelExcelService;
import cc.wdev.platform.system.i18n.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "LabelAdminController", description = "多语言控制器")
public class LabelPltController extends AbstractController {

    private LabelService labelService;

    private LabelExcelService labelExcelService;

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "获取多语言列表")
    @ApiResponse(description = "获取多语言列表")
    @PostMapping(API_V1_SYS_PREFIX + "/label/list")
    public R<Page<LabelVo>> list(@Parameter(description = "多语言查询请求") @RequestBody LabelSearchRequest request) {
        return labelService.getLabelList(request);
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "批量删除多语言")
    @ApiResponse(description = "批量删除多语言")
    @PostMapping(API_V1_SYS_PREFIX + "/label/delete")
    @OperationLog("批量删除多语言")
    public R<?> delete(@Parameter(description = "多语言删除请求") @RequestBody LabelSearchRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return R.error();
        }
        labelService.delete(request.getIds());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "多语言详情")
    @ApiResponse(description = "多语言详情")
    @PostMapping(API_V1_SYS_PREFIX + "/label/details")
    @OperationLog("多语言详情")
    public R<LabelVo> details(@Parameter(description = "多语言查询请求") @RequestBody LabelSearchRequest request) {
        return R.success(labelService.details(request));
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "保存多语言")
    @ApiResponse(description = "保存多语言")
    @PostMapping(API_V1_SYS_PREFIX + "/label/save")
    @OperationLog("保存多语言")
    public R<?> save(@Parameter(description = "多语言保存请求") @RequestBody LabelEditRequest request) {
        labelService.saveLabel(request);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "生成多语言文件")
    @ApiResponse(description = "生成多语言文件")
    @PostMapping(API_V1_SYS_PREFIX + "/label/generate")
    @OperationLog("生成多语言文件")
    public void generate(HttpServletResponse response) throws Exception {
        labelService.download("", response);
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "导出多语言文件")
    @ApiResponse(description = "导出多语言文件")
    @RequestMapping(API_V1_SYS_PREFIX + "/label/export")
    @OperationLog("导出多语言文件")
    public void exportExcel(@RequestBody LabelSearchRequest request, HttpServletResponse response) throws Exception {
        if (null != request.getIsTemplate() && request.getIsTemplate()) {
            labelExcelService.exportLabelExcelTemplate(response);
        } else {
            labelExcelService.exportLabelExcel(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "导入多语言文件")
    @ApiResponse(description = "导入多语言文件")
    @PostMapping(API_V1_SYS_PREFIX + "/label/import")
    @OperationLog("导入多语言文件")
    public R<?> importExcel(@Parameter(description = "多语言导入文件") @RequestPart("file") MultipartFile multipartFile) {
        return labelExcelService.importLabelExcel(multipartFile);
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "翻译多语言")
    @ApiResponse(description = "翻译多语言")
    @PostMapping(API_V1_SYS_PREFIX + "/label/translate")
    @OperationLog("翻译多语言")
    public R<?> translate(@Parameter(description = "多语言获取请求") LabelSearchRequest request) {
        labelService.translate(request.getIds());
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('dev:tools:i18n')")
    @Operation(summary = "标识验证")
    @ApiResponse(description = "标识验证")
    @GetMapping(API_V1_SYS_PREFIX + "/label/check-code")
    @OperationLog("标识验证")
    public R<Boolean> check(@Parameter(description = "多语言标识验证请求") LabelSearchRequest request) {
        if (null == request.getCode() || StringUtils.isEmpty(request.getCode())) {
            return R.success(Boolean.FALSE);
        }
        if (StringUtils.isNotEmpty(request.getCode())
            && StringUtils.isNotEmpty(request.getOldCode())
            && request.getCode().equalsIgnoreCase(request.getOldCode())) {
            return R.success(Boolean.TRUE);
        }
        return R.success(labelService.checkLabelCode(request.getCode()));
    }
}
