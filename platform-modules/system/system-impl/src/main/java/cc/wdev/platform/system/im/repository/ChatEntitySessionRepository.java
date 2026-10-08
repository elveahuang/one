package cc.wdev.platform.system.im.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.im.domain.entity.ChatEntitySessionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatEntitySessionRepository extends BaseEntityRepository<ChatEntitySessionEntity, Long> {
}
