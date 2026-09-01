package cc.wdev.platform.system.message.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.message.domain.entity.MessageHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface MessageHistoryRepository extends BaseEntityRepository<MessageHistoryEntity, Long> {
}
