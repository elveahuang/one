package cc.wdev.platform.system.i18n.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.i18n.domain.entity.EntityLabelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface EntityLabelRepository extends BaseEntityRepository<EntityLabelEntity, Long> {
}
