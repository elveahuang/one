package cc.wdev.platform.system.im.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author elvea
 */
@Mapper
public interface ChatSessionRepository extends BaseEntityRepository<ChatSessionEntity, Long> {
}
