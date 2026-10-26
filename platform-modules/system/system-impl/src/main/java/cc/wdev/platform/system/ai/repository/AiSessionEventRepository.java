package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface AiSessionEventRepository extends BaseEntityRepository<AiSessionEventEntity, Long> {
}
