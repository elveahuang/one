package cc.wdev.platform.system.im.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.bo.EntityDateBo;
import cc.wdev.platform.system.core.domain.bo.EntityLongBo;
import cc.wdev.platform.system.im.domain.entity.ChatMessageEntity;
import cc.wdev.platform.system.im.domain.request.ChatMessageCountRequest;
import cc.wdev.platform.system.im.domain.request.ChatPageRequest;
import cc.wdev.platform.system.im.domain.request.ChatSessionMessageCountRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author erden
 */
@Mapper
public interface ChatMessageRepository extends BaseEntityRepository<ChatMessageEntity, Long> {

    long getChatMessageCountWithEntitySession(@Param("params") ChatMessageCountRequest request);

    long getChatMessageCountWithoutEntitySession(@Param("params") ChatMessageCountRequest request);

    IPage<EntityLongBo> findLastMessageBo(Page<?> page,
                                          @Param("bizIds") Collection<Long> bizIds,
                                          @Param("bizType") String bizType,
                                          @Param("userId") Long userId,
                                          @Param("params") ChatPageRequest pageRequest);

    List<EntityLongBo> chatSessionMessageCountBos(@Param("requests") List<ChatSessionMessageCountRequest> requests);

    List<EntityLongBo> lastMessageBoList(@Param("chatSessionIds") Collection<Long> chatSessionIds);

    /**
     * 批量获取用户最近一次沟通时间
     */
    List<EntityDateBo> getLastActiveTimeBatch(@Param("userIds") Collection<Long> userIds);

}
