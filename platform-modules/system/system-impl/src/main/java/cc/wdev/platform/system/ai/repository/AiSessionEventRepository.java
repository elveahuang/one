package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author elvea
 */
@Mapper
public interface AiSessionEventRepository extends BaseEntityRepository<AiSessionEventEntity, Long> {
    /**
     * 获取会话历史记录
     */
    Page<AiSessionEventVo> findHistory(Page<?> page,
                                       @Param("userId") Long userId,
                                       @Param("aiSessionId") Long aiSessionId);
}
