package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatDeleteRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiChatVo;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;

/**
 * @author elvea
 */
public interface AiChatApi {

    /**
     * 普通对话
     */
    String chatText(SimpleChatRequest request);

    /**
     * 流式对话
     */
    Flux<String> chatStream(SimpleChatRequest request);

    /**
     * 获取单个对话记录
     */
    AiChatVo getChat(AiChatGetRequest request);

    /**
     * 删除单个对话记录
     */
    boolean deleteChat(AiChatDeleteRequest request);

    /**
     * 分页查询我的对话记录
     */
    Page<AiChatVo> findMyChats(AiChatSearchRequest request);

}
