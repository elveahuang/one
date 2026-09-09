package cc.wdev.webapp.ai.service;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

/**
 * @author elvea
 */
public interface AiService {

    /**
     * 普通对话
     */
    String chatText(SimpleChatRequest request);

    /**
     * 流式对话
     */
    Flux<String> chatStream(SimpleChatRequest request);

    /**
     * 获取对话客户端
     */
    ChatClient getChatClient();

}
