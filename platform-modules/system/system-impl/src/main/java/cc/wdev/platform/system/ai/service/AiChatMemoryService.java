package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiChatMemoryEntity;
import cc.wdev.platform.system.ai.domain.request.AiChatDeleteRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiChatVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiChatMemoryService extends CachingEntityService<AiChatMemoryEntity, Long> {

    /**
     * 查询所有会话ID
     */
    List<String> findConversationIds();

    /**
     * 查询会话记录
     */
    default List<AiChatMemoryEntity> findByConversationId(String conversationId) {
        return this.findByConversationId(conversationId, AiConstants.MAX_MEMORY_MESSAGE_COUNT);
    }

    /**
     * 查询会话记录
     */
    List<AiChatMemoryEntity> findByConversationId(String conversationId, int limit);

    /**
     * 删除会话记录
     */
    void deleteByConversationId(String conversationId);

    /**
     * 获取单个对话记录
     */
    AiChatVo getChat(AiChatGetRequest request);

    /**
     * 删除单个对话记录
     */
    void deleteChat(AiChatDeleteRequest request);

    /**
     * 分页查询我的对话记录
     */
    Page<AiChatVo> findMyChats(AiChatSearchRequest request);
}
