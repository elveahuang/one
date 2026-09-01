package cc.wdev.platform.system.im.api;

import cc.wdev.platform.system.im.domain.converter.ChatSessionConverter;
import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import cc.wdev.platform.system.im.domain.request.ChatRequest;
import cc.wdev.platform.system.im.domain.request.ChatSessionRequest;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;
import cc.wdev.platform.system.im.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSessionApiImpl implements ChatSessionApi {

    private final ChatSessionService chatSessionService;

    @Override
    public List<ChatSessionVo> getChatSessions(ChatSessionRequest request) {
        List<ChatSessionEntity> entities = chatSessionService.getChatSessions(request);
        return entities.stream().map(ChatSessionConverter.INSTANCE::entityToVo).toList();
    }

    @Override
    public long getChatSessionCount(ChatSessionRequest request) {
        return chatSessionService.getChatSessionCount(request);
    }

    @Override
    public ChatSessionVo getChatSession(ChatRequest request) {
        return chatSessionService.getChatSession(request);
    }
}
