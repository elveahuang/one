package cc.wdev.platform.system.im.api;

import cc.wdev.platform.system.im.domain.request.ChatRequest;
import cc.wdev.platform.system.im.domain.request.ChatSessionRequest;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;

import java.util.List;

public interface ChatSessionApi {
    /**
     * 根据业务ID数组批量获取最后一位目标用户ID
     */
    List<ChatSessionVo> getChatSessions(ChatSessionRequest request);

    /**
     * 获取沟通会话数
     */
    long getChatSessionCount(ChatSessionRequest request);

    /**
     * 获取沟通会话
     */
    ChatSessionVo getChatSession(ChatRequest request);
}
