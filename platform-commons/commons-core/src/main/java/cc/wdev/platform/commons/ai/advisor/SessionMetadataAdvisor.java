package cc.wdev.platform.commons.ai.advisor;

import cc.wdev.platform.commons.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.MemoryAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.session.CreateSessionRequest;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionService;
import org.springframework.core.Ordered;

import static org.springframework.ai.session.advisor.SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY;
import static org.springframework.ai.session.advisor.SessionMemoryAdvisor.USER_ID_CONTEXT_KEY;

/**
 * 排在SessionMemoryAdvisor之前，直接注入元数据到Session
 *
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class SessionMetadataAdvisor implements BaseAdvisor, MemoryAdvisor {

    public static final String DEFAULT_USER_ID = "default-user";

    private final SessionService sessionService;

    @Override
    public @NotNull ChatClientRequest before(@NotNull ChatClientRequest request, @NotNull AdvisorChain advisorChain) {
        String sessionId = MapUtils.getString(request.context(), SESSION_ID_CONTEXT_KEY, "");
        String userId = MapUtils.getString(request.context(), USER_ID_CONTEXT_KEY, "");
        if (StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
            return request;
        }

        Session session = this.sessionService.findById(sessionId);
        if (session == null) {
            Prompt prompt = request.prompt();
            UserMessage userMessage = prompt.getUserMessage();
            this.sessionService.create(CreateSessionRequest.builder()
                .id(sessionId)
                .userId(StringUtils.nvl(userId, DEFAULT_USER_ID))
                .metadata(userMessage.getMetadata())
                .build());
        }
        return request;
    }

    @Override
    public @NotNull ChatClientResponse after(@NotNull ChatClientResponse response, @NotNull AdvisorChain advisorChain) {
        return response;
    }

    /**
     * 获取优先级
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 900;
    }

}
