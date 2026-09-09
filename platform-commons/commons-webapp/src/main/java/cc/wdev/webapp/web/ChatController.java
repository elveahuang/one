package cc.wdev.webapp.web;

import cc.wdev.platform.commons.ai.domain.chat.SimpleChatResponse;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.webapp.ai.service.AiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "对话控制器")
public class ChatController {

    private final AiService aiService;

    @GetMapping("/chat/start")
    public R<SimpleChatResponse> chatStart(@RequestParam(value = "conversationId", defaultValue = "") String conversationId) {
        conversationId = StringUtils.isNotEmpty(conversationId) ? conversationId : StringUtils.uuid();
        SimpleChatResponse response = SimpleChatResponse.builder()
            .conversationId(conversationId)
            .messages(Lists.newArrayList())
            .build();
        return R.success(response);
    }

    @PostMapping("/chat/text")
    public String chatCompletionText(@RequestBody SimpleChatRequest request) {
        return this.aiService.chatText(request);
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCompletionStream(@RequestBody SimpleChatRequest request) {
        return this.aiService.chatStream(request);
    }

}
