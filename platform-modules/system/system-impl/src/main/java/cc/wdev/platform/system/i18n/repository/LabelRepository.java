package cc.wdev.platform.system.i18n.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.i18n.domain.entity.LabelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface LabelRepository extends BaseEntityRepository<LabelEntity, Long> {
}
