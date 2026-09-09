package cc.wdev.webapp.ai.service.impl;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.webapp.ai.service.AiService;
import cc.wdev.webapp.ai.tools.CoreTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private static final Resource PROMPT = new DefaultResourceLoader().getResource("classpath:/META-INF/cc.wdev/prompts/default.md");

    private final AiManager aiManager;

    private final CoreTools coreTools;

    /**
     * @see AiService#chatText(SimpleChatRequest)
     */
    @Override
    public String chatText(SimpleChatRequest request) {
        AiUtils.processChatRequest(request);

        ChatClient chatClient = this.getChatClient();
        ChatClient.ChatClientRequestSpec chatSpec = AiUtils.processChatSpec(chatClient, request);
        return chatSpec.call().content();
    }

    /**
     * @see AiService#chatStream(SimpleChatRequest)
     */
    @Override
    public Flux<String> chatStream(SimpleChatRequest request) {
        AiUtils.processChatRequest(request);

        ChatClient chatClient = this.getChatClient();
        ChatClient.ChatClientRequestSpec chatSpec = AiUtils.processChatSpec(chatClient, request);
        return chatSpec.stream().content();
    }

    /**
     * @see AiService#getChatClient()
     */
    @Override
    public ChatClient getChatClient() {
        ChatClient.Builder builder = ChatClient.builder(this.aiManager.getChatModel());
        this.aiManager.applyBaseAdvisors(builder);
        this.aiManager.applyMemoryAdvisor(builder);
        this.aiManager.applyAgentTool(builder);
        builder.defaultTools(coreTools);
        builder.defaultSystem(PROMPT);

        return builder.build();
    }

}
