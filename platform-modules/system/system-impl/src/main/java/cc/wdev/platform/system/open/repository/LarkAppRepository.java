package cc.wdev.platform.system.open.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.open.domain.entity.LarkAppEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 飞书应用 Repository
 */
@Mapper
public interface LarkAppRepository extends BaseEntityRepository<LarkAppEntity, Long> {
}
