package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface UserRoleRepository extends BaseEntityRepository<UserRoleEntity, Long> {
}
