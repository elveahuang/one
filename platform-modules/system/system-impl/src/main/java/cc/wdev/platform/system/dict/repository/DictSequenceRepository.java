package cc.wdev.platform.system.dict.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.dict.domain.entity.DictSequenceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface DictSequenceRepository extends BaseEntityRepository<DictSequenceEntity, Long> {
}
