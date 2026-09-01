package cc.wdev.platform.system.storage.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.storage.StorageFactory;
import cc.wdev.platform.commons.core.storage.model.FileObject;
import cc.wdev.platform.commons.core.storage.model.FileOptions;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StorageAccessTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Iterator;
import java.util.List;

import static cc.wdev.platform.commons.constants.AttachmentConstants.DEFAULT_EDITOR_EXT;
import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "AttachmentController", description = "附件控制器")
public class AttachmentWebController extends AbstractController {

    private final StorageFactory storageFactory;

    private final AttachmentApi attachmentApi;

    @Authenticated
    @Operation(summary = "获取附件类型")
    @ApiResponse(description = "获取附件类型")
    @GetMapping(API_V1_PREFIX + "/attachment/type")
    public R<AttachmentVo> getAttachmentType(AttachmentRequest request) {
        AttachmentVo attachmentType = this.attachmentApi.getAttachmentType(request);
        return R.success(attachmentType);
    }

    @Authenticated
    @Operation(summary = "获取附件")
    @ApiResponse(description = "获取附件")
    @GetMapping(API_V1_PREFIX + "/attachment")
    public R<AttachmentVo> get(AttachmentRequest request) {
        AttachmentVo attachment = attachmentApi.getAttachment(request);
        return R.success(attachment);
    }

    @Authenticated
    @OperationLog("附件上传")
    @Operation(summary = "附件上传")
    @ApiResponse(description = "附件上传")
    @PostMapping(API_V1_PREFIX + "/attachment/upload")
    public R<?> uploadAttachment(AttachmentRequest request, MultipartHttpServletRequest servletRequest) {
        // 获取待上传文件集合
        List<MultipartFile> files = Lists.newArrayList();
        Iterator<String> it = servletRequest.getFileNames();
        while (it.hasNext()) {
            files.add(servletRequest.getFile(it.next()));
        }

        // 获取附件类型，检查上传文件是否符合要求
        AttachmentVo vo = this.attachmentApi.getAttachmentType(AttachmentRequest.builder().bizType(request.getBizType()).build());
        if (vo.getBizType().equalsIgnoreCase(AttachmentBizTypeEnum.NONE.getCode())) {
            throw new ServiceException(ResponseCodeEnum.ATTACHMENT_LIMIT_ERROR);
        }
        for (MultipartFile file : Lists.newArrayList(files)) {
            if (file == null || StringUtils.isEmpty(file.getOriginalFilename())
                || !FilenameUtils.isExtension(file.getOriginalFilename().toLowerCase(), vo.getConfig().getExtensions())) {
                throw new ServiceException(ResponseCodeEnum.ATTACHMENT_LIMIT_ERROR);
            }
        }

        // 上传附件
        List<AttachmentFileVo> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(files)) {
            for (MultipartFile file : files) {
                result.add(this.attachmentApi.uploadAttachment(request, file));
            }
        }
        return R.success(result);
    }

    /**
     * 编辑器附件上传
     */
    @Authenticated
    @OperationLog("编辑器附件上传")
    @Operation(summary = "编辑器附件上传")
    @ApiResponse(description = "编辑器附件上传")
    @PostMapping(API_V1_PREFIX + "/attachment/editor/upload")
    public R<?> uploadEditorAttachment(@Parameter(description = "文件") @RequestParam("file") MultipartFile file) throws Exception {
        if (StringUtils.isNotEmpty(file.getOriginalFilename()) && FilenameUtils.isExtension(file.getOriginalFilename().toLowerCase(), DEFAULT_EDITOR_EXT)) {
            FileOptions parameter = FileOptions.builder()
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .accessType(StorageAccessTypeEnum.PUBLIC)
                .size(file.getSize())
                .build();
            FileObject<?> object = this.storageFactory.getStorageService().uploadFile(file, parameter);
            return R.success(object.getUrl());
        }
        throw new ServiceException(ResponseCodeEnum.ATTACHMENT_LIMIT_ERROR);
    }

}
