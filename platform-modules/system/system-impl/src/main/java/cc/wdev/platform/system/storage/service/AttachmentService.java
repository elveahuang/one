package cc.wdev.platform.system.storage.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.repository.AttachmentRepository;

/**
 * @author elvea
 */
public interface AttachmentService extends CachingEntityService<AttachmentEntity, Long>, EnhancedEntityService<AttachmentEntity, Long, AttachmentRepository> {
}
