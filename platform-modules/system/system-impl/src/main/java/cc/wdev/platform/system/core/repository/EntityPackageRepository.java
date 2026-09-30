package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.entity.EntityPackageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface EntityPackageRepository extends BaseEntityRepository<EntityPackageEntity, Long> {
}
