package cc.wdev.platform.system.security.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.security.domain.entity.ClientEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface ClientRepository extends BaseEntityRepository<ClientEntity, Long> {
}
