package cc.wdev.platform.system.storage.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.storage.domain.entity.AttachmentRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface AttachmentRelationRepository extends BaseEntityRepository<AttachmentRelationEntity, Long> {
}
