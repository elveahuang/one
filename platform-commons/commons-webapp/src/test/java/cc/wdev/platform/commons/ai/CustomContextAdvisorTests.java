package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.advisor.CustomContextAdvisor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * CustomContextAdvisor 单元测试：验证 AssistantMessage 继承用户消息上下文
 *
 * @author elvea
 */
public class CustomContextAdvisorTests {

    @Test
    public void adviseCallInjectsUserContextTest() {
        UserMessage userMessage = UserMessage.builder()
            .text("你好")
            .metadata(Map.of(
                AiConstants.METADATA_USER_ID, "1",
                AiConstants.METADATA_TENANT_ID, 1L,
                AiConstants.METADATA_CHAT_TYPE, "AGENT",
                AiConstants.METADATA_AGENT_CODE, "default"
            ))
            .build();
        AssistantMessage assistantMessage = new AssistantMessage("回复内容");
        ChatClientRequest request = new ChatClientRequest(new Prompt(List.of(userMessage)), Map.of());
        ChatClientResponse response = new ChatClientResponse(
            new ChatResponse(List.of(new Generation(assistantMessage))), Map.of());

        CallAdvisorChain chain = Mockito.mock(CallAdvisorChain.class);
        Mockito.when(chain.nextCall(request)).thenReturn(response);

        CustomContextAdvisor advisor = new CustomContextAdvisor();
        ChatClientResponse result = advisor.adviseCall(request, chain);

        Map<String, Object> metadata = result.chatResponse().getResult().getOutput().getMetadata();
        Assertions.assertEquals(1L, metadata.get(AiConstants.METADATA_USER_ID));
        Assertions.assertEquals(1L, metadata.get(AiConstants.METADATA_TENANT_ID));
        Assertions.assertEquals("AGENT", metadata.get(AiConstants.METADATA_CHAT_TYPE));
        Assertions.assertEquals("default", metadata.get(AiConstants.METADATA_AGENT_CODE));
    }

    @Test
    public void adviseStreamInjectsUserContextTest() {
        UserMessage userMessage = UserMessage.builder()
            .text("你好")
            .metadata(Map.of(
                AiConstants.METADATA_USER_ID, "2",
                AiConstants.METADATA_CHAT_TYPE, "KB"
            ))
            .build();
        AssistantMessage assistantMessage = new AssistantMessage("流式回复");
        ChatClientRequest request = new ChatClientRequest(new Prompt(List.of(userMessage)), Map.of());
        ChatClientResponse response = new ChatClientResponse(
            new ChatResponse(List.of(new Generation(assistantMessage))), Map.of());

        StreamAdvisorChain chain = Mockito.mock(StreamAdvisorChain.class);
        Mockito.when(chain.nextStream(request)).thenReturn(Flux.just(response));

        CustomContextAdvisor advisor = new CustomContextAdvisor();
        ChatClientResponse result = advisor.adviseStream(request, chain).blockLast();

        Assertions.assertNotNull(result);
        Map<String, Object> metadata = result.chatResponse().getResult().getOutput().getMetadata();
        Assertions.assertEquals(2L, metadata.get(AiConstants.METADATA_USER_ID));
        Assertions.assertEquals("KB", metadata.get(AiConstants.METADATA_CHAT_TYPE));
    }

}
