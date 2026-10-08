package cc.wdev.platform.system.dict.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.dict.domain.entity.DictEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface DictRepository extends BaseEntityRepository<DictEntity, Long> {
}
