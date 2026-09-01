package cc.wdev.platform.system.open.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.open.domain.entity.DingtalkAppEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钉钉应用 Repository
 */
@Mapper
public interface DingtalkAppRepository extends BaseEntityRepository<DingtalkAppEntity, Long> {
}
