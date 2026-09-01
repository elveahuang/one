package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.entity.UserBizRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author danexie
 */
@Mapper
public interface UserBizRelationRepository extends BaseEntityRepository<UserBizRelationEntity, Long> {
}
