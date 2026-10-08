package cc.wdev.platform.system.im.api;

import cc.wdev.platform.system.im.domain.request.ChatEntitySessionMarkRequest;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionRequest;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionSearchRequest;
import cc.wdev.platform.system.im.domain.vo.ChatEntitySessionVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ChatEntitySessionApi {

    /**
     * 置顶会话沟通
     * 注意！！！此方法，如果不存在实体会话，会新建实体会话，需在外层先校验是否拥有该会话的消息收发权限
     */
    void doTop(ChatEntitySessionRequest request);

    /**
     * 取消置顶会话沟通
     */
    void undoTop(ChatEntitySessionRequest request);

    /**
     * 取消置顶会话沟通
     */
    void batchUndoTop(List<ChatEntitySessionRequest> requests);

    /**
     * 收藏职位沟通
     */
    ChatEntitySessionVo doCollect(ChatEntitySessionRequest request);

    /**
     * 取消收藏职位沟通
     */
    ChatEntitySessionVo undoCollect(ChatEntitySessionRequest request);

    /**
     * 取消收藏职位沟通
     */
    List<ChatEntitySessionVo> batchUndoCollect(List<ChatEntitySessionRequest> requests);

    /**
     * 删除会话沟通并清除聊天记录
     * 注意！！！此方法，如果不存在实体会话，会新建实体会话，需在外层先校验是否拥有该会话的消息收发权限
     */
    void clear(ChatEntitySessionRequest request);

    /**
     * 重新开启已删除的会话沟通
     */
    void reopen(Long sid, Collection<Long> userIds);

    /**
     * 批量获取会话沟通是否置顶
     */
    Map<Long, Integer> chatSessionTopIndMap(Collection<Long> chatSessionIds, Long userId);

    /**
     * 获取会话实体
     */
    ChatEntitySessionVo getChatEntitySession(ChatEntitySessionRequest request);

    /**
     * 获取会话实体
     */
    List<ChatEntitySessionVo> getChatEntitySessions(ChatEntitySessionSearchRequest request);

    /**
     * 标记会话实体
     */
    ChatEntitySessionVo markChatEntitySession(ChatEntitySessionMarkRequest request);

    /**
     * 获取当前会话实体
     */
    ChatEntitySessionVo getChatEntitySession(Long sid);

    /**
     * 批量获取会话实体信息
     */
    Map<Long, ChatEntitySessionVo> chatEntitySessionMap(Collection<Long> chatSessionIds, Long userId);
}
