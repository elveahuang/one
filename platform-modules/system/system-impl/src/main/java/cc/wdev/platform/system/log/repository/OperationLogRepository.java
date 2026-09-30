package cc.wdev.platform.system.log.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.log.domain.entity.OperationLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface OperationLogRepository extends BaseEntityRepository<OperationLogEntity, Long> {
}
