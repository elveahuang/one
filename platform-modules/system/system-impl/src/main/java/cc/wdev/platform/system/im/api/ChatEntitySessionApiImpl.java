package cc.wdev.platform.system.im.api;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.system.im.domain.converter.ChatEntitySessionConverter;
import cc.wdev.platform.system.im.domain.entity.ChatEntitySessionEntity;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionMarkRequest;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionRequest;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionSearchRequest;
import cc.wdev.platform.system.im.domain.vo.ChatEntitySessionVo;
import cc.wdev.platform.system.im.service.ChatEntitySessionService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatEntitySessionApiImpl implements ChatEntitySessionApi {

    private final ChatEntitySessionService chatEntitySessionService;

    @Override
    public void doTop(ChatEntitySessionRequest request) {
        chatEntitySessionService.doTop(request);
    }

    @Override
    public void undoTop(ChatEntitySessionRequest request) {
        chatEntitySessionService.undoTop(request);
    }

    @Override
    public void batchUndoTop(List<ChatEntitySessionRequest> requests) {
        chatEntitySessionService.batchUndoTop(requests);
    }

    @Override
    public ChatEntitySessionVo doCollect(ChatEntitySessionRequest request) {
        ChatEntitySessionEntity entity = chatEntitySessionService.doCollect(request);
        return ChatEntitySessionConverter.INSTANCE.entityToVo(entity);
    }

    @Override
    public ChatEntitySessionVo undoCollect(ChatEntitySessionRequest request) {
        ChatEntitySessionEntity entity = chatEntitySessionService.undoCollect(request);
        return ChatEntitySessionConverter.INSTANCE.entityToVo(entity);
    }

    @Override
    public List<ChatEntitySessionVo> batchUndoCollect(List<ChatEntitySessionRequest> requests) {
        List<ChatEntitySessionEntity> entities = chatEntitySessionService.batchUndoCollect(requests);
        return entities.stream().map(ChatEntitySessionConverter.INSTANCE::entityToVo).toList();
    }

    @Override
    public void clear(ChatEntitySessionRequest request) {
        chatEntitySessionService.clear(request);
    }

    @Override
    public void reopen(Long sid, Collection<Long> userIds) {
        chatEntitySessionService.reopen(sid, userIds);
    }

    @Override
    public Map<Long, Integer> chatSessionTopIndMap(Collection<Long> chatSessionIds, Long userId) {
        return chatEntitySessionService.chatSessionTopIndMap(chatSessionIds, userId);
    }

    @Override
    public ChatEntitySessionVo getChatEntitySession(ChatEntitySessionRequest request) {
        ChatEntitySessionEntity entity = chatEntitySessionService.getChatEntitySession(request);
        return ChatEntitySessionConverter.INSTANCE.entityToVo(entity);
    }

    @Override
    public List<ChatEntitySessionVo> getChatEntitySessions(ChatEntitySessionSearchRequest request) {
        List<ChatEntitySessionEntity> entities = chatEntitySessionService.getChatEntitySessions(request);
        return entities.stream().map(ChatEntitySessionConverter.INSTANCE::entityToVo).toList();
    }

    @Override
    public ChatEntitySessionVo markChatEntitySession(ChatEntitySessionMarkRequest request) {
        ChatEntitySessionEntity entity = chatEntitySessionService.markChatEntitySession(request);
        return ChatEntitySessionConverter.INSTANCE.entityToVo(entity);
    }

    @Override
    public ChatEntitySessionVo getChatEntitySession(Long sid) {
        ChatEntitySessionEntity entitySessionEntity = chatEntitySessionService.getChatEntitySession(ChatEntitySessionRequest.builder()
            .chatSessionId(sid)
            .userId(SecurityUtils.getUid())
            .build());
        return ChatEntitySessionConverter.INSTANCE.entityToVo(entitySessionEntity);
    }

    @Override
    public Map<Long, ChatEntitySessionVo> chatEntitySessionMap(Collection<Long> chatSessionIds, Long userId) {
        Map<Long, ChatEntitySessionEntity> entityMap = chatEntitySessionService.chatEntitySessionMap(chatSessionIds, userId);
        if (CollectionUtils.isEmpty(entityMap)) {
            return Collections.emptyMap();
        }
        Map<Long, ChatEntitySessionVo> voMap = Maps.newHashMapWithExpectedSize(entityMap.size());
        for (Long key : entityMap.keySet()) {
            ChatEntitySessionEntity entity = entityMap.get(key);
            if (!ObjectUtils.isValidId(entity)) {
                continue;
            }
            voMap.put(key, ChatEntitySessionConverter.INSTANCE.entityToVo(entity));
        }
        return voMap;
    }
}
