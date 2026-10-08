package cc.wdev.platform.commons.ai.advisor;

import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class CustomContextAdvisor implements CallAdvisor, StreamAdvisor {

    @NonNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 150;
    }

    @NonNull
    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, CallAdvisorChain chain) {
        // 从UserMessage的metadata提取上下文相关数据
        Map<String, Object> map = extractContext(request);
        // 继续执行下一个Advisor
        ChatClientResponse response = chain.nextCall(request);
        // 注入上下文相关数据
        if (MapUtils.isNotEmpty(map)) {
            injectUserContext(response.chatResponse(), map);
        }
        return response;
    }

    @NonNull
    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest advisedRequest, StreamAdvisorChain chain) {
        // 从UserMessage的metadata提取上下文相关数据
        Map<String, Object> map = extractContext(advisedRequest);
        // 继续执行下一个Advisor
        return chain.nextStream(advisedRequest).doOnNext(advisedResponse -> {
            if (MapUtils.isNotEmpty(map) && advisedResponse.chatResponse() != null) {
                injectUserContext(advisedResponse.chatResponse(), map);
            }
        });
    }

    private Map<String, Object> extractContext(ChatClientRequest request) {
        Map<String, Object> map = Maps.newHashMap();
        for (Message message : request.prompt().getInstructions()) {
            if (message.getMessageType() == MessageType.USER) {
                Long userId = MapUtils.getLong(message.getMetadata(), AiConstants.METADATA_USER_ID);
                if (ObjectUtils.isValidId(userId)) {
                    map.put(AiConstants.METADATA_USER_ID, userId);
                }

                Long tenantId = MapUtils.getLong(message.getMetadata(), AiConstants.METADATA_TENANT_ID);
                if (ObjectUtils.isValidId(tenantId)) {
                    map.put(AiConstants.METADATA_TENANT_ID, tenantId);
                }

                String chatType = MapUtils.getString(message.getMetadata(), AiConstants.METADATA_CHAT_TYPE);
                if (StringUtils.isNotEmpty(chatType)) {
                    map.put(AiConstants.METADATA_CHAT_TYPE, chatType);
                }

                String agentCode = MapUtils.getString(message.getMetadata(), AiConstants.METADATA_AGENT_CODE);
                if (StringUtils.isNotEmpty(agentCode)) {
                    map.put(AiConstants.METADATA_AGENT_CODE, agentCode);
                }
            }
        }
        return map;
    }

    /**
     * 注入上下文
     */
    private void injectUserContext(ChatResponse chatResponse, Map<String, Object> map) {
        if (chatResponse == null || chatResponse.getResults().isEmpty()) {
            return;
        }

        chatResponse.getResults().forEach(result -> {
            AssistantMessage assistantMessage = result.getOutput();
            map.forEach((key, value) -> {
                if (!assistantMessage.getMetadata().containsKey(key)) {
                    assistantMessage.getMetadata().put(key, value);
                }
            });
        });
    }

}
