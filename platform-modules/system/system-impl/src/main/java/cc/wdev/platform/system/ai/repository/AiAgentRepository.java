package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiAgentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface AiAgentRepository extends BaseEntityRepository<AiAgentEntity, Long> {
}
