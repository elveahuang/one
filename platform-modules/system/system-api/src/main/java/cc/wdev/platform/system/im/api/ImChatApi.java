package cc.wdev.platform.system.im.api;

import cc.wdev.platform.system.core.domain.bo.EntityLongBo;
import cc.wdev.platform.system.im.domain.request.*;
import cc.wdev.platform.system.im.domain.vo.ChatEntitySessionVo;
import cc.wdev.platform.system.im.domain.vo.ChatMessageVo;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;
import cc.wdev.platform.system.im.domain.vo.ChatUserVo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface ImChatApi {

    /**
     * 检查会话
     */
    ChatSessionVo getChatSession(ChatRequest request);

    /**
     * 获取会话额外信息
     */
    List<ChatSessionVo> buildChatSessionPage(Long userId, List<EntityLongBo> bos);

    /**
     * 批量获取会话
     *
     * @return 会话VO列表（每条记录对应一个 (bizId, userId) 组合）
     */
    List<ChatSessionVo> list(ChatSearchRequest request);

    /**
     * 批量更新会话
     */
    void saveBatchChatSession(List<ChatSaveRequest> requests);

    /**
     * 开始会话
     */
    ChatSessionVo saveChatSession(ChatRequest request);

    /**
     * 标记会话
     * 1. 标记最新的消息为已读
     */
    void markChatSession(ChatEntitySessionMarkRequest request);

    /**
     * 获取全部消息数量
     */
    long getChatMessageCount(ChatMessageCountRequest request);

    /**
     * 获取单个会话全部消息数量
     */
    long getChatSessionMessageCount(ChatSessionMessageCountRequest request);

    /**
     * 批量获取会话未读消息数量
     */
    Map<Long, Long> chatSessionMessageCountMap(Collection<Long> chatSessionIds, Long userId);

    /**
     * 批量获取会话最新消息
     */
    Map<Long, ChatMessageVo> lastChatMessageMap(Collection<Long> chatSessionIds);

    /**
     * 分页查询会话和对应最后一条最新消息
     */
    Page<ChatSessionVo> findChatSessions(Collection<Long> bizIds, String bizType, ChatPageRequest pageRequest);

    /**
     * 分页查询聊天记录
     */
    Page<ChatMessageVo> findChatMessages(ChatMessageRequest request);

    /**
     * 保存信息
     */
    ChatMessageVo getChatMessage(ChatMessageRequest request);

    /**
     * 保存信息
     */
    ChatMessageVo saveChatMessage(ChatMessageSaveRequest request);

    /**
     * 保存实体消息，用于记录用户对于消息的操作，如已读、已回复等
     */
    void saveChatEntityMessage(ChatEntityMessageRequest request);

    /**
     * 获取用户信息
     */
    ChatUserVo getChatUser(ChatUserRequest request);

    /**
     * 获取用户信息
     */
    ChatUserVo getChatUser(Long entityId);

    /**
     * 批量获取用户信息
     */
    Map<Long, ChatUserVo> batchChatUser(Collection<Long> userIds);

    /**
     * 批量获取会话实体信息
     */
    Map<Long, ChatEntitySessionVo> batchChatEntitySession(Collection<Long> chatSessionIds, Long userId);

    /**
     * 置顶会话沟通
     */
    void doTop(Long sid);

    /**
     * 取消置顶会话沟通
     */
    void undoTop(Long sid);

    /**
     * 取消置顶会话沟通
     */
    void batchUndoTop(Long sid, Collection<Long> userIds);

    /**
     * 收藏职位沟通
     */
    ChatEntitySessionVo doCollect(Long sid);

    /**
     * 取消收藏职位沟通
     */
    ChatEntitySessionVo undoCollect(Long sid);

    /**
     * 取消收藏职位沟通
     */
    List<ChatEntitySessionVo> batchUndoCollect(Long sid, Collection<Long> userIds);

    /**
     * 删除会话沟通并清除聊天记录
     */
    void clear(Long sid);

    /**
     * 重新开启已删除的会话沟通
     */
    void reopen(Long sid, Collection<Long> userIds);

    /**
     * 批量获取会话沟通是否置顶
     */
    Map<Long, Integer> chatSessionTopIndMap(Collection<Long> chatSessionIds, Long userId);

    /**
     * 获取最近活跃时间
     */
    LocalDateTime getLastActiveTime(Long userId);

    /**
     * 批量获取最近活跃时间
     */
    Map<Long, LocalDateTime> getLastActiveTimeBatch(Collection<Long> userIds);

}
