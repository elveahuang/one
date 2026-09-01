package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiApiKeyEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppKeyRepository extends BaseEntityRepository<AiApiKeyEntity, Long> {
}
