package cc.wdev.platform.system.region.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.region.domain.entity.AddressRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface AddressRelationRepository extends BaseEntityRepository<AddressRelationEntity, Long> {
}
