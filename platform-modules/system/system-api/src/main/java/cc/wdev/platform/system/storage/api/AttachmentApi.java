package cc.wdev.platform.system.storage.api;

import cc.wdev.platform.commons.core.storage.model.FileOptions;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface AttachmentApi {

    /**
     * 获取附件类型
     */
    AttachmentVo getAttachmentType(AttachmentRequest request);

    /**
     * 获取附件
     */
    AttachmentVo getAttachment(AttachmentRequest request);

    /**
     * 上传附件
     */
    AttachmentFileVo uploadAttachment(AttachmentRequest request, MultipartFile file);

    /**
     * 上传附件
     */
    AttachmentFileVo uploadAttachment(AttachmentRequest request, Resource resource);

    /**
     * 上传附件
     */
    AttachmentFileVo uploadAttachment(AttachmentRequest request, File file);

    /**
     * 上传附件
     */
    AttachmentFileVo uploadAttachment(AttachmentRequest request, InputStream is, FileOptions options);

    /**
     * 保存文件关联
     */
    void saveAttachmentRelation(AttachmentRelationRequest request);

    /**
     * 删除文件关联并软删除附件文件（孤儿文件清理）
     */
    void deleteAttachmentRelation(AttachmentRelationRequest request);

    /**
     * 获取附件文件
     */
    List<AttachmentFileVo> getAttachmentFile(AttachmentRequest request);

    /**
     * 批量获取附件
     */
    Map<Long, AttachmentVo> getAttachmentBatch(AttachmentRequest request);

}
