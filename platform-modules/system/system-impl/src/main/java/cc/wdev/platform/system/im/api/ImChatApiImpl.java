package cc.wdev.platform.system.im.api;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.bo.EntityLongBo;
import cc.wdev.platform.system.core.domain.dto.UserInfoDto;
import cc.wdev.platform.system.core.domain.vo.UserInfoVo;
import cc.wdev.platform.system.im.domain.converter.ChatSessionConverter;
import cc.wdev.platform.system.im.domain.entity.ChatEntityMessageEntity;
import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import cc.wdev.platform.system.im.domain.request.*;
import cc.wdev.platform.system.im.domain.vo.*;
import cc.wdev.platform.system.im.service.ChatEntityMessageService;
import cc.wdev.platform.system.im.service.ChatMessageContentService;
import cc.wdev.platform.system.im.service.ChatMessageService;
import cc.wdev.platform.system.im.service.ChatSessionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class ImChatApiImpl implements ImChatApi {

    private final ChatSessionService chatSessionService;

    private final ChatMessageService chatMessageService;

    private final ChatEntitySessionApi chatEntitySessionApi;

    private final ChatMessageContentService chatMessageContentService;

    private final ChatEntityMessageService chatEntityMessageService;

    private final UserApi userApi;

    /**
     * @see ImChatApi#getChatSession(ChatRequest)
     */
    public ChatSessionVo getChatSession(ChatRequest request) {
        return chatSessionService.getChatSession(request);
    }

    @Override
    public List<ChatSessionVo> buildChatSessionPage(Long userId, List<EntityLongBo> bos) {
        List<Long> chatSessionIds = Lists.newArrayListWithCapacity(bos.size());
        List<Long> chatMessageIds = Lists.newArrayListWithCapacity(bos.size());
        List<ChatSessionVo> voList = Lists.newArrayListWithCapacity(bos.size());

        for (EntityLongBo bo : bos) {
            chatSessionIds.add(bo.getId());
            chatMessageIds.add(bo.getNumber());
        }
        // 批量获取chatSession
        Map<Long, ChatSessionVo> chatSessionMap = chatSessionService.chatSessionMap(chatSessionIds);

        // 批量获取chatMessage
        Map<Long, ChatMessageVo> lastChatMessageMap = chatMessageService.messageMap(chatMessageIds);
        Map<Long, ChatMessageContentVo> lastChatMessageContentMap = chatMessageContentService.messageContentMap(chatMessageIds);

        for (EntityLongBo bo : bos) {
            ChatSessionVo vo = chatSessionMap.get(bo.getId());
            if (vo == null) {
                continue;
            }
            vo.setId(bo.getId());
            ChatMessageVo lastMessageVo = lastChatMessageMap.get(bo.getNumber());
            ChatMessageContentVo lastMessageContentVo = lastChatMessageContentMap.get(bo.getNumber());
            if (lastMessageVo != null) {
                if (lastMessageContentVo != null) {
                    lastMessageVo.setContent(lastMessageContentVo.getContent());
                    lastMessageVo.setExtra(lastMessageContentVo.getExtra());
                }
                vo.setLastMessage(lastMessageVo);
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * @see ImChatApi#list(ChatSearchRequest)
     */
    @Override
    public List<ChatSessionVo> list(ChatSearchRequest request) {
        List<ChatSessionEntity> entities = chatSessionService.list(request);
        return entities.stream().map(ChatSessionConverter.INSTANCE::entityToVo).toList();
    }

    /**
     * @see ImChatApi#saveBatchChatSession(List)
     */
    @Override
    public void saveBatchChatSession(List<ChatSaveRequest> requests) {
        chatSessionService.saveBatchChatSession(requests);
    }

    /**
     * @see ImChatApi#saveChatSession(ChatRequest)
     */
    public ChatSessionVo saveChatSession(ChatRequest request) {
        if (StringUtils.isBlank(request.getBizType())
            || !ObjectUtils.isValidId(request.getBizId(), request.getTargetUserId(), request.getUserId())) {

            log.error("Invalid Chat Session Save Request. bizType : [{}], bizId : [{}], userId : [{}], targetUserId : [{}]",
                request.getBizType(), request.getBizId(), request.getUserId(), request.getTargetUserId());
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }

        ChatSessionVo chatSessionVo = chatSessionService.saveChatSession(request);

        ChatEntitySessionVo entitySession = chatEntitySessionApi.getChatEntitySession(ChatEntitySessionRequest.builder()
            .chatSessionId(chatSessionVo.getId())
            .userId(request.getEntitySessionUserId())
            .build());
        chatSessionVo.setLastReadMessageId(entitySession.getLastReadMessageId());
        chatSessionVo.setTopInd(entitySession.getTopInd());
        chatSessionVo.setCollectInd(entitySession.getCollectInd());
        chatSessionVo.setChatEntitySessionId(entitySession.getId());
        return chatSessionVo;
    }

    /**
     * @see ImChatApi#findChatMessages(ChatMessageRequest)
     */
    @Override
    public Page<ChatMessageVo> findChatMessages(ChatMessageRequest request) {
        // 实体会话
        ChatEntitySessionVo entitySessionVo = this.chatEntitySessionApi.getChatEntitySession(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());
        request.setClearAt(Optional.ofNullable(entitySessionVo.getClearAt()).orElse(LocalDateTime.MIN));

        Page<ChatMessageVo> page = this.chatMessageService.findChatMessages(request);
        if (page.isEmpty()) {
            return page;
        }
        // 批量查询消息内容
        List<Long> chatMessageIdList = page.getContent().stream().map(ChatMessageVo::getId).toList();
        Map<Long, ChatMessageContentVo> chatMessageContentMap = chatMessageContentService.messageContentMap(chatMessageIdList);
        for (ChatMessageVo message : page.getContent()) {
            ChatMessageContentVo content = chatMessageContentMap.get(message.getId());
            if (content == null) {
                continue;
            }
            message.setContent(content.getContent());
            message.setExtra(content.getExtra());
        }
        return page;
    }

    /**
     * @see ImChatApi#getChatMessage(ChatMessageRequest)
     */
    @Override
    public ChatMessageVo getChatMessage(ChatMessageRequest request) {
        return this.chatMessageService.getChatMessage(request);
    }

    /**
     * @see ImChatApi#getChatMessage(ChatMessageRequest)
     */
    @Override
    public void markChatSession(ChatEntitySessionMarkRequest request) {
        this.chatEntitySessionApi.markChatEntitySession(request);
    }

    /**
     * @see ImChatApi#getChatMessage(ChatMessageRequest)
     */
    @Override
    public long getChatMessageCount(ChatMessageCountRequest request) {
        return this.chatMessageService.getChatMessageCount(request);
    }

    /**
     * @see ImChatApi#getChatMessageCount(ChatMessageCountRequest)
     */
    @Override
    public long getChatSessionMessageCount(ChatSessionMessageCountRequest request) {
        ChatEntitySessionRequest chatEntitySessionRequest = ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build();
        Long lastChatMessageId = 0L;
        LocalDateTime clearAt = LocalDateTime.MIN;

        ChatEntitySessionVo chatEntitySession = this.chatEntitySessionApi.getChatEntitySession(chatEntitySessionRequest);
        if (chatEntitySession != null) {
            lastChatMessageId = chatEntitySession.getLastReadMessageId();
            if (chatEntitySession.getClearAt() != null) {
                clearAt = chatEntitySession.getClearAt();
            }
        }

        request.setLastReadMessageId(lastChatMessageId);
        request.setClearAt(clearAt);
        return this.chatMessageService.getChatSessionMessageCount(request);
    }

    @Override
    public Map<Long, Long> chatSessionMessageCountMap(Collection<Long> chatSessionIds, Long userId) {
        if (CollectionUtils.isEmpty(chatSessionIds) || !ObjectUtils.isValidId(userId)) {
            return Collections.emptyMap();
        }
        Map<Long, ChatEntitySessionVo> chatEntitySessionMap = this.batchChatEntitySession(chatSessionIds, userId);
        List<ChatSessionMessageCountRequest> requests = Lists.newArrayListWithCapacity(chatEntitySessionMap.size());
        for (Long chatSessionId : chatSessionIds) {
            ChatEntitySessionVo sessionVo = chatEntitySessionMap.get(chatSessionId);
            ChatSessionMessageCountRequest request = ChatSessionMessageCountRequest.builder()
                .chatSessionId(chatSessionId)
                .build();
            if (null == sessionVo) {
                request.setLastReadMessageId(0L);
                request.setClearAt(LocalDateTime.MIN);
                requests.add(request);
                continue;
            }
            request.setLastReadMessageId(sessionVo.getLastReadMessageId());
            request.setClearAt(Optional.ofNullable(sessionVo.getClearAt()).orElse(LocalDateTime.MIN));
            requests.add(request);
        }

        return chatMessageService.chatSessionMessageCountMap(requests);
    }

    @Override
    public Map<Long, ChatMessageVo> lastChatMessageMap(Collection<Long> chatSessionIds) {
        if (CollectionUtils.isEmpty(chatSessionIds)) {
            return Collections.emptyMap();
        }
        List<EntityLongBo> bos = chatMessageService.lastMessageBoList(chatSessionIds);
        // 批量获取chatMessage
        List<Long> chatMessageIds = bos.stream().map(EntityLongBo::getNumber).collect(Collectors.toList());
        Map<Long, ChatMessageVo> lastChatMessageMap = chatMessageService.messageMap(chatMessageIds);
        Map<Long, ChatMessageContentVo> lastChatMessageContentMap = chatMessageContentService.messageContentMap(chatMessageIds);
        Map<Long, ChatMessageVo> voMap = Maps.newHashMapWithExpectedSize(bos.size());
        for (EntityLongBo bo : bos) {
            Long messageId = bo.getNumber();
            ChatMessageVo messageVo = lastChatMessageMap.get(messageId);
            if (messageVo == null) {
                continue;
            }
            ChatMessageContentVo contentVo = lastChatMessageContentMap.get(messageId);
            messageVo.setContent(contentVo.getContent());
            messageVo.setExtra(contentVo.getExtra());
            voMap.put(bo.getId(), messageVo);
        }
        return voMap;
    }

    @Override
    public Page<ChatSessionVo> findChatSessions(Collection<Long> bizIds, String bizType, ChatPageRequest pageRequest) {
        if (CollectionUtils.isEmpty(bizIds)) {
            return Page.empty(pageRequest.getPageable());
        }
        Long userId = SecurityUtils.getUid();
        Page<EntityLongBo> page = chatMessageService.findLastMessageBo(bizIds, bizType, userId, pageRequest);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return Page.empty(pageRequest.getPageable());
        }

        return WebUtils.newPage(page, toChatSessionPage(page.getContent()));
    }

    private List<ChatSessionVo> toChatSessionPage(List<EntityLongBo> bos) {
        List<Long> chatSessionIds = Lists.newArrayListWithCapacity(bos.size());
        List<Long> chatMessageIds = Lists.newArrayListWithCapacity(bos.size());
        List<ChatSessionVo> voList = Lists.newArrayListWithCapacity(bos.size());

        for (EntityLongBo bo : bos) {
            chatSessionIds.add(bo.getId());
            chatMessageIds.add(bo.getNumber());
        }
        // 批量获取chatSession
        Map<Long, ChatSessionVo> chatSessionMap = chatSessionService.chatSessionMap(chatSessionIds);

        // 批量获取chatMessage
        Map<Long, ChatMessageVo> lastChatMessageMap = chatMessageService.messageMap(chatMessageIds);
        Map<Long, ChatMessageContentVo> lastChatMessageContentMap = chatMessageContentService.messageContentMap(chatMessageIds);

        for (EntityLongBo bo : bos) {
            ChatSessionVo vo = chatSessionMap.get(bo.getId());
            if (vo == null) {
                continue;
            }
            vo.setId(bo.getId());
            ChatMessageVo lastMessageVo = lastChatMessageMap.get(bo.getNumber());
            ChatMessageContentVo lastMessageContentVo = lastChatMessageContentMap.get(bo.getNumber());
            lastMessageVo.setContent(lastMessageContentVo.getContent());
            lastMessageVo.setExtra(lastMessageContentVo.getExtra());
            vo.setLastMessage(lastMessageVo);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * @see ImChatApi#saveChatMessage(ChatMessageSaveRequest)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ChatMessageVo saveChatMessage(ChatMessageSaveRequest request) {
        // 保存信息
        ChatMessageVo chatMessageVo = this.chatMessageService.saveChatMessage(request);
        // 保存信息内容
        chatMessageContentService.saveChatMessageContent(ChatMessageContentSaveRequest.builder()
            .chatSessionId(chatMessageVo.getChatSessionId())
            .chatMessageId(chatMessageVo.getId())
            .content(request.getContent())
            .extra(request.getExtra())
            .type(request.getType())
            .build());
        chatMessageVo.setContent(request.getContent());
        chatMessageVo.setExtra(nvl(request.getExtra()));

        // 修改会话表的最新消息ID
        ChatSessionEntity update = ChatSessionEntity.builder()
            .lastMessageId(chatMessageVo.getId())
            .lastMessageSendAt(chatMessageVo.getCreatedAt())
            .build();
        update.setId(request.getChatSessionId());
        update.setUpdatedAt(LocalDateTime.now());
        update.setUpdatedBy(request.getSenderUserId());
        this.chatSessionService.updateById(update);

        return chatMessageVo;
    }

    /**
     * @see ImChatApi#saveChatEntityMessage(ChatEntityMessageRequest)
     */
    @Override
    public void saveChatEntityMessage(ChatEntityMessageRequest request) {
        List<ChatEntityMessageEntity> chatEntityMessageList = this.chatEntityMessageService.findChatEntityMessage(request);
        Map<Long, ChatEntityMessageEntity> chatEntityMessageMap = chatEntityMessageList.stream()
            .collect(Collectors.toMap(ChatEntityMessageEntity::getId, Function.identity()));

        List<ChatEntityMessageEntity> entityList = Lists.newArrayList();
        request.getChatMessageIdList().forEach(id -> {
            ChatEntityMessageEntity entityMessageEntity;
            if (chatEntityMessageMap.containsKey(id)) {
                entityMessageEntity = chatEntityMessageMap.get(id);
                entityMessageEntity.setReadInd(1);
            } else {
                entityMessageEntity = new ChatEntityMessageEntity();
                entityMessageEntity.setChatSessionId(request.getChatSessionId());
                entityMessageEntity.setChatMessageId(id);
                entityMessageEntity.setUserId(request.getUserId());
                entityMessageEntity.setReadInd(1);
                entityMessageEntity.setStatus(1);
            }
            entityList.add(entityMessageEntity);
        });
        this.chatEntityMessageService.saveBatch(entityList);
    }

    /**
     * @see ImChatApi#getChatUser(ChatUserRequest)
     */
    @Override
    public ChatUserVo getChatUser(ChatUserRequest request) {
        UserInfoDto userInfoDto = this.userApi.getUserInfo(request.getUserId());
        if (userInfoDto == null) {
            return null;
        }
        return ChatUserVo.builder()
            .uid(userInfoDto.getId())
            .name(userInfoDto.getDisplayName())
            .avatarUrl(userInfoDto.getAvatarUrl())
            .sex(userInfoDto.getSex())
            .build();
    }

    /**
     * @see ImChatApi#getChatUser(Long)
     */
    @Override
    public ChatUserVo getChatUser(Long entityId) {
        return getChatUser(ChatUserRequest.builder().userId(entityId).build());
    }

    /**
     * @see ImChatApi#batchChatUser(Collection)
     */
    @Override
    public Map<Long, ChatUserVo> batchChatUser(Collection<Long> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return Collections.emptyMap();
        }

        Map<Long, UserInfoVo> userInfoMap = this.userApi.batchUserInfo(entityIds);
        return userInfoMap.values().stream().collect(Collectors.toMap(UserInfoVo::getId, e -> ChatUserVo.builder()
            .uid(e.getId())
            .name(e.getDisplayName())
            .avatarUrl(e.getAvatarUrl())
            .sex(e.getSex())
            .build()));
    }

    @Override
    public Map<Long, ChatEntitySessionVo> batchChatEntitySession(Collection<Long> chatSessionIds, Long userId) {
        if (CollectionUtils.isEmpty(chatSessionIds)) {
            return Collections.emptyMap();
        }
        return chatEntitySessionApi.chatEntitySessionMap(chatSessionIds, userId);
    }

    @Override
    public void doTop(Long sid) {
        if (!ObjectUtils.isValidId(sid)) {
            return;
        }
        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        chatEntitySessionApi.doTop(ChatEntitySessionRequest.builder()
            .chatSessionId(sid)
            .userId(SecurityUtils.getUid())
            .build());
    }

    @Override
    public void undoTop(Long sid) {
        if (!ObjectUtils.isValidId(sid)) {
            return;
        }
        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        chatEntitySessionApi.undoTop(ChatEntitySessionRequest.builder()
            .chatSessionId(sid)
            .userId(SecurityUtils.getUid())
            .build());
    }

    @Override
    public void batchUndoTop(Long sid, Collection<Long> userIds) {
        if (!ObjectUtils.isValidId(sid) || CollectionUtils.isEmpty(userIds)) {
            return;
        }
        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        List<ChatEntitySessionRequest> requests = Lists.newArrayListWithCapacity(userIds.size());
        for (Long userId : userIds) {
            ChatEntitySessionRequest request = ChatEntitySessionRequest.builder()
                .chatSessionId(sid)
                .userId(userId)
                .build();
            requests.add(request);
        }
        chatEntitySessionApi.batchUndoTop(requests);
    }

    @Override
    public ChatEntitySessionVo doCollect(Long sid) {
        if (!ObjectUtils.isValidId(sid)) {
            return null;
        }
        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        return chatEntitySessionApi.doCollect(ChatEntitySessionRequest.builder()
            .chatSessionId(sid)
            .userId(SecurityUtils.getUid())
            .build());
    }

    @Override
    public ChatEntitySessionVo undoCollect(Long sid) {
        if (!ObjectUtils.isValidId(sid)) {
            return null;
        }
        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        return chatEntitySessionApi.undoCollect(ChatEntitySessionRequest.builder()
            .chatSessionId(sid)
            .userId(SecurityUtils.getUid())
            .build());
    }

    @Override
    public List<ChatEntitySessionVo> batchUndoCollect(Long sid, Collection<Long> userIds) {
        if (!ObjectUtils.isValidId(sid) || CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        List<ChatEntitySessionRequest> requests = Lists.newArrayListWithCapacity(userIds.size());
        for (Long userId : userIds) {
            ChatEntitySessionRequest request = ChatEntitySessionRequest.builder()
                .chatSessionId(sid)
                .userId(userId)
                .build();
            requests.add(request);
        }
        return chatEntitySessionApi.batchUndoCollect(requests);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void clear(Long sid) {
        if (!ObjectUtils.isValidId(sid)) {
            return;
        }

        // 校验会话是否存在
        chatSessionService.checkExistsOrFail(sid, ResponseCodeEnum.NOT_PRESENT);

        chatEntitySessionApi.clear(ChatEntitySessionRequest.builder()
            .chatSessionId(sid)
            .userId(SecurityUtils.getUid())
            .build());
    }

    @Override
    public void reopen(Long sid, Collection<Long> userIds) {
        if (!ObjectUtils.isValidId(sid) || CollectionUtils.isEmpty(userIds)) {
            return;
        }
        chatEntitySessionApi.reopen(sid, userIds);
    }

    @Override
    public Map<Long, Integer> chatSessionTopIndMap(Collection<Long> chatSessionIds, Long userId) {
        return chatEntitySessionApi.chatSessionTopIndMap(chatSessionIds, userId);
    }

    /**
     * @see ImChatApi#getLastActiveTime(Long)
     */
    @Override
    public LocalDateTime getLastActiveTime(Long userId) {
        return chatMessageService.getLastActiveTime(userId);
    }

    /**
     * @see ImChatApi#getLastActiveTimeBatch(Collection)
     */
    @Override
    public Map<Long, LocalDateTime> getLastActiveTimeBatch(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        return chatMessageService.getLastActiveTimeBatch(userIds);
    }

}
