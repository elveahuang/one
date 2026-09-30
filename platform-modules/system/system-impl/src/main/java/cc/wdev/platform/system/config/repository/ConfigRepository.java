package cc.wdev.platform.system.config.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface ConfigRepository extends BaseEntityRepository<ConfigEntity, Long> {
}
