package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author elvea
 */
@Mapper
public interface AiSessionEventRepository extends BaseEntityRepository<AiSessionEventEntity, Long> {
    @InterceptorIgnore(tenantLine = "true")
    List<AiSessionEventEntity> findTurnHead(@Param("sessionId") String sessionId,
                                            @Param("firstEventId") String firstEventId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to,
                                            @Param("messageTypes") List<String> messageTypes,
                                            @Param("excludeSynthetic") boolean excludeSynthetic,
                                            @Param("excludeArchived") boolean excludeArchived);

}
