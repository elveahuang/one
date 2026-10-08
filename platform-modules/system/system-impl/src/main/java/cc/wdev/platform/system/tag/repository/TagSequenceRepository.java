package cc.wdev.platform.system.tag.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.tag.domain.entity.TagSequenceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface TagSequenceRepository extends BaseEntityRepository<TagSequenceEntity, Long> {
}
