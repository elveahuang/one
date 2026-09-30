package cc.wdev.platform.system.im.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.im.domain.entity.ChatMessageContentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface ChatMessageContentRepository extends BaseEntityRepository<ChatMessageContentEntity, Long> {
}
