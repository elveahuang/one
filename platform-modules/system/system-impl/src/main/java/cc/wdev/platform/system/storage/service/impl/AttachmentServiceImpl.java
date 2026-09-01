package cc.wdev.platform.system.storage.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.repository.AttachmentRepository;
import cc.wdev.platform.system.storage.service.AttachmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AttachmentServiceImpl
    extends BaseCachingEntityService<AttachmentEntity, Long, AttachmentRepository> implements AttachmentService {
}
