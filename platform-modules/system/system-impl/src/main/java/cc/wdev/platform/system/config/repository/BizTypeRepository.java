package cc.wdev.platform.system.config.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.config.domain.entity.BizTypeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface BizTypeRepository extends BaseEntityRepository<BizTypeEntity, Long> {
}
