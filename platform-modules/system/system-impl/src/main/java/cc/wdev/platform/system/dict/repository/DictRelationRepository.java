package cc.wdev.platform.system.dict.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.dict.domain.entity.DictRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface DictRelationRepository extends BaseEntityRepository<DictRelationEntity, Long> {
}
