package cc.wdev.platform.system.im.api;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.bo.EntityLongBo;
import cc.wdev.platform.system.core.domain.dto.UserInfoDto;
import cc.wdev.platform.system.core.domain.vo.UserInfoVo;
import cc.wdev.platform.system.core.socket.SystemWebSocketService;
import cc.wdev.platform.system.im.domain.converter.ChatSessionConverter;
import cc.wdev.platform.system.im.domain.entity.ChatEntityMessageEntity;
import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import cc.wdev.platform.system.im.domain.request.*;
import cc.wdev.platform.system.im.domain.vo.*;
import cc.wdev.platform.system.im.enums.ChatRespMessageTypeEnum;
import cc.wdev.platform.system.im.service.ChatEntityMessageService;
import cc.wdev.platform.system.im.service.ChatMessageContentService;
import cc.wdev.platform.system.im.service.ChatMessageService;
import cc.wdev.platform.system.im.service.ChatSessionService;
import cc.wdev.platform.system.im.utils.ImUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
     * WebSocket 推送服务（未启用 WS 时为 null，推送逻辑需容错）
     */
    private final ObjectProvider<SystemWebSocketService> systemWebSocketServiceProvider;

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
        Long currentUserId = SecurityUtils.getUid();
        return entities.stream()
            .map(entity -> this.toSessionVo(entity, currentUserId))
            .toList();
    }

    /**
     * 会话转 VO，并按当前用户解析出对端用户。
     *
     * <p>会话只存一条记录（收发共用），对端就是 userId / targetUserId 中不等于当前用户的那个；
     * 都不等于（例如管理员查看他人会话）时退回 targetUserId。</p>
     */
    private ChatSessionVo toSessionVo(ChatSessionEntity entity, Long currentUserId) {
        ChatSessionVo vo = ChatSessionConverter.INSTANCE.entityToVo(entity);
        if (Objects.equals(entity.getUserId(), currentUserId)) {
            vo.setOppositeUserId(entity.getTargetUserId());
        } else if (Objects.equals(entity.getTargetUserId(), currentUserId)) {
            vo.setOppositeUserId(entity.getUserId());
        } else {
            vo.setOppositeUserId(entity.getTargetUserId());
        }
        return vo;
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
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ChatSessionVo saveChatSession(ChatRequest request) {
        // 发起人一律取当前登录用户，不信任入参
        Long currentUserId = SecurityUtils.getUid();
        request.setUserId(currentUserId);
        if (StringUtils.isBlank(request.getBizType())
            || !ObjectUtils.isValidId(request.getBizId(), request.getTargetUserId(), request.getUserId())) {

            log.error("Invalid Chat Session Save Request. bizType : [{}], bizId : [{}], userId : [{}], targetUserId : [{}]",
                request.getBizType(), request.getBizId(), request.getUserId(), request.getTargetUserId());
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        if (Objects.equals(request.getUserId(), request.getTargetUserId())) {
            // 不允许和自己建会话
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }

        // 同一对用户（不分方向）只保留一条会话：消息按 chat_session_id 归属，
        // 若为收发双方各建一条，一次对话会被拆成两条互不相通的线程（B 看不到 A 的消息）。
        ChatSessionEntity existing = this.findSessionByPair(request.getBizType(), request.getBizId(),
            request.getUserId(), request.getTargetUserId());
        ChatSessionVo chatSessionVo;
        if (existing != null) {
            chatSessionVo = ChatSessionConverter.INSTANCE.entityToVo(existing);
        } else {
            chatSessionVo = chatSessionService.saveChatSession(request);
        }

        ChatEntitySessionVo entitySession = chatEntitySessionApi.getChatEntitySession(ChatEntitySessionRequest.builder()
            .chatSessionId(chatSessionVo.getId())
            .userId(request.getEntitySessionUserId())
            .build());
        chatSessionVo.setLastReadMessageId(entitySession.getLastReadMessageId());
        chatSessionVo.setTopInd(entitySession.getTopInd());
        chatSessionVo.setCollectInd(entitySession.getCollectInd());
        chatSessionVo.setChatEntitySessionId(entitySession.getId());
        // 会话只存一条（收发共用），对端即目标用户
        chatSessionVo.setOppositeUserId(request.getTargetUserId());
        return chatSessionVo;
    }

    /**
     * 按业务类型/业务实体/用户对（不分收发方向）查找已存在的会话
     */
    private ChatSessionEntity findSessionByPair(String bizType, Long bizId, Long userId, Long targetUserId) {
        return this.chatSessionService.findByPair(bizType, bizId, userId, targetUserId);
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
        // 未清空过会话时 clearAt 为空，**不要**兜底成 LocalDateTime.MIN：
        // 该值（-999999999 年）超出 PostgreSQL timestamp 的可表示范围，参与比较会得到异常结果，
        // 反而把全部历史消息过滤掉。留空即表示「不按时间水位过滤」。
        if (entitySessionVo.getClearAt() != null) {
            request.setClearAt(entitySessionVo.getClearAt());
        } else {
            request.setClearAt(null);
        }

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
        // 未清空会话时保持 null（不按时间水位过滤）；不要用 LocalDateTime.MIN，该值超出 PG timestamp 范围
        LocalDateTime clearAt = null;

        ChatEntitySessionVo chatEntitySession = this.chatEntitySessionApi.getChatEntitySession(chatEntitySessionRequest);
        if (chatEntitySession != null) {
            lastChatMessageId = chatEntitySession.getLastReadMessageId();
            clearAt = chatEntitySession.getClearAt();
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
                // 带上用户才能排除「自己发的消息」不计入未读
                .userId(userId)
                .build();
            if (null == sessionVo) {
                request.setLastReadMessageId(0L);
                // 未清空：保持 null，不按时间水位过滤（LocalDateTime.MIN 超出 PG timestamp 范围）
                request.setClearAt(null);
                requests.add(request);
                continue;
            }
            request.setLastReadMessageId(sessionVo.getLastReadMessageId());
            request.setClearAt(sessionVo.getClearAt());
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
        // 归属校验：发送人必须是当前登录用户（不信任入参）；接收方由 findOwnedSession 按会话解析
        Long currentUserId = SecurityUtils.getUid();
        request.setSenderUserId(currentUserId);
        ChatSessionEntity session = this.findOwnedSession(request);
        request.setChatSessionId(session.getId());
        // 注意：接收方已在 findOwnedSession 内解析为「会话中的另一个人」，此处不可再用 targetUserId 覆盖，
        // 否则反向用户（B）发消息时收件人会变成自己。
        Long receiverUserId = request.getReceiverUserId();

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
        update.setId(session.getId());
        update.setUpdatedAt(LocalDateTime.now());
        update.setUpdatedBy(request.getSenderUserId());
        this.chatSessionService.updateById(update);

        // 事务提交后实时推送给接收方（离线场景由历史消息接口兜底）
        this.publishAfterCommit(receiverUserId, chatMessageVo);

        return chatMessageVo;
    }

    /**
     * 解析并校验会话归属，同时算出本条消息的真实接收方。
     *
     * <p>会话只存一条（收发共用），所以「我的会话」= 我是 userId 或 targetUserId；
     * 接收方则是另一个人。会话不存在时不允许隐式创建，避免用任意 targetUserId 反向建会话越权发信。</p>
     */
    private ChatSessionEntity findOwnedSession(ChatMessageSaveRequest request) {
        Long currentUserId = SecurityUtils.getUid();
        ChatSessionEntity session = this.chatSessionService.findById(request.getChatSessionId());
        if (session == null) {
            throw new ServiceException(ResponseCodeEnum.CHAT__SESSION_NOT_PRESENT);
        }
        Long receiverUserId = this.resolveOppositeUserId(session, currentUserId);
        if (!ObjectUtils.isValidId(receiverUserId)) {
            // 既不是发送方也不是接收方
            throw new ServiceException(ResponseCodeEnum.OPERATION_PERMISSION_ERROR);
        }
        // 把解析结果回传给调用方（写消息与推送都用它）
        request.setReceiverUserId(receiverUserId);
        return session;
    }

    /**
     * 按当前用户解析对端用户；当前用户不在会话内时返回 null
     */
    private Long resolveOppositeUserId(ChatSessionEntity session, Long currentUserId) {
        if (Objects.equals(session.getUserId(), currentUserId)) {
            return session.getTargetUserId();
        }
        if (Objects.equals(session.getTargetUserId(), currentUserId)) {
            return session.getUserId();
        }
        return null;
    }

    /**
     * 事务提交后经 WebSocket 点对点推送新消息；无事务或无 WS 时安全跳过
     */
    private void publishAfterCommit(Long receiverUserId, ChatMessageVo chatMessageVo) {
        SystemWebSocketService webSocketService = this.systemWebSocketServiceProvider.getIfAvailable();
        if (webSocketService == null || !ObjectUtils.isValidId(receiverUserId)) {
            return;
        }
        Runnable publish = () -> {
            try {
                webSocketService.publish(ImUtils.createJsonMessage(
                    List.of(receiverUserId),
                    ImUtils.createChatRespMessage(ChatRespMessageTypeEnum.MESSAGE.getValue(), chatMessageVo)));
            } catch (Exception e) {
                // 推送失败不影响已落库的消息
                log.warn("IM message websocket publish failed. sessionId - [{}].", chatMessageVo.getChatSessionId(), e);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            publish.run();
        }
    }

    /**
     * @see ImChatApi#saveChatEntityMessage(ChatEntityMessageRequest)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveChatEntityMessage(ChatEntityMessageRequest request) {
        Long currentUserId = SecurityUtils.getUid();
        request.setUserId(currentUserId);
        if (!ObjectUtils.isValidId(request.getChatSessionId())
            || CollectionUtils.isEmpty(request.getChatMessageIdList())) {
            return;
        }
        ChatSessionEntity session = this.chatSessionService.findById(request.getChatSessionId());
        if (session == null || !Objects.equals(session.getUserId(), currentUserId)) {
            // 只能标记自己会话里的消息
            throw new ServiceException(ResponseCodeEnum.CHAT__SESSION_NOT_PRESENT);
        }

        List<ChatEntityMessageEntity> chatEntityMessageList = this.chatEntityMessageService.findChatEntityMessage(request);
        // 以业务消息 ID 为键（与入参、落库字段一致）
        Map<Long, ChatEntityMessageEntity> chatEntityMessageMap = chatEntityMessageList.stream()
            .collect(Collectors.toMap(ChatEntityMessageEntity::getChatMessageId, Function.identity(), (a, b) -> a));

        List<ChatEntityMessageEntity> entityList = Lists.newArrayList();
        request.getChatMessageIdList().forEach(id -> {
            ChatEntityMessageEntity entityMessageEntity = chatEntityMessageMap.get(id);
            if (entityMessageEntity != null) {
                entityMessageEntity.setReadInd(1);
            } else {
                entityMessageEntity = new ChatEntityMessageEntity();
                entityMessageEntity.setChatSessionId(request.getChatSessionId());
                entityMessageEntity.setChatMessageId(id);
                entityMessageEntity.setUserId(currentUserId);
                entityMessageEntity.setReadInd(1);
                entityMessageEntity.setStatus(1);
            }
            entityList.add(entityMessageEntity);
        });
        this.chatEntityMessageService.saveBatch(entityList);

        // 向会话发起方推送已读回执（让发送侧知道对方读到了哪）
        Long readerUserId = currentUserId;
        Long chatSessionId = request.getChatSessionId();
        Long lastReadMessageId = request.getChatMessageIdList().stream().max(Long::compareTo).orElse(null);
        this.publishReadAfterCommit(session.getTargetUserId(), chatSessionId, readerUserId, lastReadMessageId);
    }

    /**
     * 已读回执载荷（须可序列化，WebSocket 会做 JSON 序列化）
     */
    private record ChatReadReceipt(Long chatSessionId, Long readerUserId, Long lastReadMessageId) implements java.io.Serializable {
    }

    /**
     * 事务提交后推送已读回执
     */
    private void publishReadAfterCommit(Long receiverUserId, Long chatSessionId, Long readerUserId, Long lastReadMessageId) {
        SystemWebSocketService webSocketService = this.systemWebSocketServiceProvider.getIfAvailable();
        if (webSocketService == null || !ObjectUtils.isValidId(receiverUserId)) {
            return;
        }
        ChatReadReceipt payload = new ChatReadReceipt(
            chatSessionId, readerUserId, lastReadMessageId == null ? 0L : lastReadMessageId);
        Runnable publish = () -> {
            try {
                webSocketService.publish(ImUtils.createJsonMessage(
                    List.of(receiverUserId),
                    ImUtils.createChatRespMessage(ChatRespMessageTypeEnum.READ.getValue(), payload)));
            } catch (Exception e) {
                log.warn("IM read receipt publish failed. sessionId - [{}].", chatSessionId, e);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            publish.run();
        }
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
