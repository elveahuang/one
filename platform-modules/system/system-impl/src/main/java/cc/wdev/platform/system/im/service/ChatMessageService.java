package cc.wdev.platform.system.im.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.core.domain.bo.EntityLongBo;
import cc.wdev.platform.system.im.domain.entity.ChatMessageEntity;
import cc.wdev.platform.system.im.domain.request.*;
import cc.wdev.platform.system.im.domain.vo.ChatMessageVo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author erden
 */
public interface ChatMessageService extends EntityService<ChatMessageEntity, Long> {

    /**
     * 分页查询对话消息
     */
    Page<ChatMessageVo> findChatMessages(ChatMessageRequest request);

    /**
     * 获取沟通消息
     */
    ChatMessageVo getChatMessage(ChatMessageRequest request);

    /**
     * 保存沟通消息
     */
    ChatMessageVo saveChatMessage(ChatMessageSaveRequest request);

    /**
     * 批量保存沟通消息
     *
     * @return Map<sessionId, ChatMessageVo>
     */
    Map<Long, ChatMessageVo> saveBatchChatMessage(Collection<ChatMessageSaveRequest> requests);

    /**
     * 检查聊天室是否在沟通中
     */
    boolean checkCommunicating(Long roomId);

    /**
     * 获取全部未读消息数量
     */
    long getChatMessageCount(ChatMessageCountRequest request);

    /**
     * 获取会话未读消息数量
     */
    long getChatSessionMessageCount(ChatSessionMessageCountRequest request);

    /**
     * 批量获取会话未读消息数量
     */
    Map<Long, Long> chatSessionMessageCountMap(List<ChatSessionMessageCountRequest> requests);

    /**
     * 分页查询会话和对应最后一条最新消息
     */
    Page<EntityLongBo> findLastMessageBo(Collection<Long> bizIds, String bizType, Long userId, ChatPageRequest pageRequest);

    /**
     * 批量查询会话最新消息
     */
    List<EntityLongBo> lastMessageBoList(Collection<Long> chatSessionIds);

    /**
     * 批量获取消息内容
     */
    Map<Long, ChatMessageVo> messageMap(Collection<Long> messageIds);

    /**
     * 根据用户ID获取最近沟通时间
     */
    LocalDateTime getLastActiveTime(Long userId);

    /**
     * 根据用户ID数组批量获取最近沟通时间
     */
    Map<Long, LocalDateTime> getLastActiveTimeBatch(Collection<Long> userIds);

}
