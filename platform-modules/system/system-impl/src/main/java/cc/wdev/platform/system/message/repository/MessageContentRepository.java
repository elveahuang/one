package cc.wdev.platform.system.message.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.message.domain.entity.MessageContentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface MessageContentRepository extends BaseEntityRepository<MessageContentEntity, Long> {
}
