package cc.wdev.platform.system.message.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.message.domain.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface MessageRepository extends BaseEntityRepository<MessageEntity, Long> {
}
