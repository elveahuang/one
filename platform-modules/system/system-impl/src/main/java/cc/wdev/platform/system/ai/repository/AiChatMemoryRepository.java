package cc.wdev.platform.system.ai.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.ai.domain.entity.AiChatMemoryEntity;
import cc.wdev.platform.system.ai.domain.request.AiChatSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiChatVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author elvea
 */
@Mapper
public interface AiChatMemoryRepository extends BaseEntityRepository<AiChatMemoryEntity, Long> {

    /**
     * 使用窗口函数获取所有符合条件的对话记录
     */
    IPage<AiChatVo> findAllChatsWithWindowFunction(Page<?> page, @Param("request") AiChatSearchRequest request);

}
