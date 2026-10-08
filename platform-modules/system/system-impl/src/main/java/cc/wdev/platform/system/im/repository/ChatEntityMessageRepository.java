package cc.wdev.platform.system.im.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.im.domain.entity.ChatEntityMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface ChatEntityMessageRepository extends BaseEntityRepository<ChatEntityMessageEntity, Long> {
}
