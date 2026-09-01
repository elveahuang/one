package cc.wdev.platform.system.tag.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.tag.domain.entity.TagRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author irving
 */
@Mapper
public interface TagRelationRepository extends BaseEntityRepository<TagRelationEntity, Long> {
}
