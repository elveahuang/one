package cc.wdev.platform.commons.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
public class CustomLoggingAdvisor implements CallAdvisor, StreamAdvisor {

    @NonNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @NonNull
    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest advisedRequest, CallAdvisorChain chain) {
        log.debug("AI adviseCall.");
        ChatClientResponse advisedResponse = chain.nextCall(this.logRequest(advisedRequest));
        this.logResponses(advisedResponse);
        return advisedResponse;
    }

    @NonNull
    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest advisedRequest, StreamAdvisorChain chain) {
        log.debug("AI adviseStream.");
        Flux<ChatClientResponse> advisedResponses = chain.nextStream(this.logRequest(advisedRequest));
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(advisedResponses, this::logResponses);
    }

    private ChatClientRequest logRequest(ChatClientRequest request) {
        // 日志红线：不记录完整提示词（可能包含 RAG 上下文/知识库原文），仅记录截断后的摘要
        String contents = String.valueOf(request.prompt().getContents());
        log.debug("AI Request: {}", truncate(contents, 200));
        log.debug("AI Request Message: {}", truncate(String.valueOf(request.context()), 200));
        return request;
    }

    private void logResponses(ChatClientResponse advisedResponse) {
        ChatResponse response = advisedResponse.chatResponse();
        if (null == response) {
            log.debug("AI Response is null");
            return;
        }

        ChatResponseMetadata responseMetadata = response.getMetadata();
        Usage usage = responseMetadata.getUsage();
        log.debug("Tokens ：{}", usage.getTotalTokens());
        log.debug("Input tokens：{}", usage.getPromptTokens());
        log.debug("Output tokens：{}", usage.getCompletionTokens());

        AssistantMessage assistantMessage = response.getResult().getOutput();
        List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
        log.debug("Tools: {}", toolCallList.size());
        if (log.isDebugEnabled()) {
            toolCallList.forEach(toolCall -> log.debug("Tool：{}", toolCall.name()));
        }
        log.debug("AI Response: {}", truncate(assistantMessage.getText(), 200));
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "...[truncated]";
    }

}
