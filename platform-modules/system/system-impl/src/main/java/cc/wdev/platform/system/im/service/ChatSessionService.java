package cc.wdev.platform.system.im.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import cc.wdev.platform.system.im.domain.request.ChatRequest;
import cc.wdev.platform.system.im.domain.request.ChatSaveRequest;
import cc.wdev.platform.system.im.domain.request.ChatSearchRequest;
import cc.wdev.platform.system.im.domain.request.ChatSessionRequest;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ChatSessionService extends CachingEntityService<ChatSessionEntity, Long> {

    /**
     * 获取会话
     */
    ChatSessionVo getChatSession(ChatRequest request);

    /**
     * 批量获取会话
     */
    List<ChatSessionEntity> list(@Valid ChatSearchRequest request);

    /**
     * 保存会话
     */
    ChatSessionVo saveChatSession(ChatRequest request);

    /**
     * 批量保存会话
     */
    void saveBatchChatSession(List<ChatSaveRequest> request);

    /**
     * 批量获取会话Map
     */
    Map<Long, ChatSessionVo> chatSessionMap(Collection<Long> sessionIds);

    /**
     * 获取业务ID列表
     */
    List<ChatSessionEntity> getChatSessions(ChatSessionRequest request);

    /**
     * 获取会话数量
     */
    long getChatSessionCount(ChatSessionRequest request);
}
