package cc.wdev.platform.system.log.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.log.domain.entity.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface LoginLogRepository extends BaseEntityRepository<LoginLogEntity, Long> {
}
