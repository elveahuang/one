package cc.wdev.platform.system.log.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.log.domain.entity.ApplicationLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface ApplicationLogRepository extends BaseEntityRepository<ApplicationLogEntity, Long> {
}
