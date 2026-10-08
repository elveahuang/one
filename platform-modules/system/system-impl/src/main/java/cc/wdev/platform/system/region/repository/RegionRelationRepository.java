package cc.wdev.platform.system.region.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.region.domain.entity.RegionRelationEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegionRelationRepository extends BaseEntityRepository<RegionRelationEntity, Long> {
}
