package cc.wdev.platform.system.log.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.log.domain.entity.UrlLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface UrlLogRepository extends BaseEntityRepository<UrlLogEntity, Long> {
}
