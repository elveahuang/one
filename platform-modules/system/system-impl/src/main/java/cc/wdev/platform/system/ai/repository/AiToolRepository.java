package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiToolEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dev
 */
@Mapper
public interface AiToolRepository extends BaseEntityRepository<AiToolEntity, Long> {
}
