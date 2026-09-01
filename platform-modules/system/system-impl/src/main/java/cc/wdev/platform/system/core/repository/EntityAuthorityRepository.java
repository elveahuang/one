package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.entity.EntityAuthorityEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EntityAuthorityRepository extends BaseEntityRepository<EntityAuthorityEntity, Long> {
}
