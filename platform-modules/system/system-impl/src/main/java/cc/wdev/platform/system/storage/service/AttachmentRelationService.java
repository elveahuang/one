package cc.wdev.platform.system.storage.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.storage.domain.entity.AttachmentRelationEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;

import java.util.List;

/**
 * @author elvea
 */
public interface AttachmentRelationService extends CachingEntityService<AttachmentRelationEntity, Long> {

    /**
     * 查询已有文件关联
     */
    List<AttachmentRelationEntity> getAttachmentRelation(AttachmentRelationRequest request);

    /**
     * 清空已有文件关联
     */
    void deleteAttachmentRelation(AttachmentRelationRequest request);

}
