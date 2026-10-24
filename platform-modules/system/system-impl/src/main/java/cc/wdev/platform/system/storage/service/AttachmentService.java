package cc.wdev.platform.system.storage.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentSearchRequest;
import cc.wdev.platform.system.storage.repository.AttachmentRepository;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AttachmentService extends CachingEntityService<AttachmentEntity, Long>, EnhancedEntityService<AttachmentEntity, Long, AttachmentRepository> {

    /**
     * 获取附件分页列表
     */
    Page<AttachmentEntity> findByPage(AttachmentSearchRequest request);

    /**
     * 删除附件
     */
    void deleteAttachments(List<Long> ids);

}
