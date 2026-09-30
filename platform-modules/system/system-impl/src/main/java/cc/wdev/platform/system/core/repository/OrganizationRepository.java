package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.entity.OrganizationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface OrganizationRepository extends BaseEntityRepository<OrganizationEntity, Long> {
}
