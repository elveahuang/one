package cc.wdev.platform.system.catalog.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.catalog.domain.entity.CatalogRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface CatalogRelationRepository extends BaseEntityRepository<CatalogRelationEntity, Long> {
}
