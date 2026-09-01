package cc.wdev.platform.system.im.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.core.domain.bo.EntityDateBo;
import cc.wdev.platform.system.core.domain.bo.EntityLongBo;
import cc.wdev.platform.system.im.domain.converter.ChatMessageConverter;
import cc.wdev.platform.system.im.domain.entity.ChatMessageEntity;
import cc.wdev.platform.system.im.domain.request.*;
import cc.wdev.platform.system.im.domain.vo.ChatMessageVo;
import cc.wdev.platform.system.im.enums.ChatMessageContentTypeEnum;
import cc.wdev.platform.system.im.repository.ChatMessageRepository;
import cc.wdev.platform.system.im.service.ChatMessageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class ChatMessageServiceImpl extends BaseEntityService<ChatMessageEntity, Long, ChatMessageRepository> implements ChatMessageService {

    /**
     * @see ChatMessageService#findChatMessages(ChatMessageRequest)
     */
    @Override
    public Page<ChatMessageVo> findChatMessages(ChatMessageRequest request) {
        LambdaQueryChainWrapper<ChatMessageEntity> wrapper = this.lambdaQueryWrapper()
            .le(ObjectUtils.isValidId(request.getLastMessageId()), ChatMessageEntity::getId, request.getLastMessageId())
            .eq(ChatMessageEntity::getChatSessionId, request.getChatSessionId())
            .eq(ChatMessageEntity::getStatus, StatusTypeEnum.ON.getValue())
            .gt(ChatMessageEntity::getCreatedAt, request.getClearAt())
            .orderByDesc(ChatMessageEntity::getCreatedAt);
        IPage<ChatMessageEntity> page = this.findPageByWrapper(getMyBatisPlusPage(request.getPageable()), wrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<ChatMessageVo> list = page.getRecords().stream()
                .map(ChatMessageConverter.INSTANCE::entityToVo)
                .toList();
            return toSpringDataPage(page, list);
        }
        return SpringDataUtils.emptyPage(request.getPageable());
    }

    /**
     * @see ChatMessageService#getChatMessage(ChatMessageRequest)
     */
    @Override
    public ChatMessageVo getChatMessage(ChatMessageRequest request) {
        if (request.getChatMessageId() != null && request.getChatMessageId() > 0) {
            ChatMessageEntity entity = this.findById(request.getChatMessageId());
            return ChatMessageConverter.INSTANCE.entityToVo(entity);
        }
        return null;
    }

    /**
     * @see ChatMessageService#saveChatMessage(ChatMessageSaveRequest)
     */
    @Override
    public ChatMessageVo saveChatMessage(ChatMessageSaveRequest request) {
        ChatMessageEntity entity = ChatMessageConverter.INSTANCE.requestToEntity(request);
        entity.setMessageContentType(Optional.ofNullable(request.getType()).orElse(ChatMessageContentTypeEnum.TEXT.getValue()));
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);
        return ChatMessageConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see ChatMessageService#saveBatchChatMessage(Collection)
     */
    @Override
    public Map<Long, ChatMessageVo> saveBatchChatMessage(Collection<ChatMessageSaveRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return Collections.emptyMap();
        }

        List<ChatMessageEntity> entities = Lists.newArrayListWithCapacity(requests.size());
        for (ChatMessageSaveRequest request : requests) {
            ChatMessageEntity entity = ChatMessageConverter.INSTANCE.requestToEntity(request);
            entity.setMessageContentType(Optional.ofNullable(request.getType()).orElse(ChatMessageContentTypeEnum.TEXT.getValue()));
            entity.setActive(ActiveTypeEnum.ENABLED.getValue());
            entities.add(entity);
        }
        this.saveBatch(entities);
        Map<Long, ChatMessageVo> voMap = Maps.newHashMapWithExpectedSize(entities.size());
        for (ChatMessageEntity entity : entities) {
            voMap.put(entity.getChatSessionId(), ChatMessageConverter.INSTANCE.entityToVo(entity));
        }
        return voMap;
    }

    /**
     * @see ChatMessageService#checkCommunicating(Long)
     */
    @Override
    public boolean checkCommunicating(Long chatSessionId) {
        long count = this.lambdaQueryWrapper()
            .select(ChatMessageEntity::getId)
            .eq(ChatMessageEntity::getChatSessionId, chatSessionId)
            .count();
        return count > 1;
    }

    /**
     * @see ChatMessageService#checkCommunicating(Long)
     */
    @Override
    public long getChatMessageCount(ChatMessageCountRequest request) {
        long countWithoutEntitySession = 0L;
        if (CollectionUtils.isEmpty(request.getBizIds())) {
            return countWithoutEntitySession;
        }

        long countWithEntitySession = this.mapper.getChatMessageCountWithEntitySession(request);
        if (CollectionUtils.isNotEmpty(request.getBizIds())) {
            countWithoutEntitySession = this.mapper.getChatMessageCountWithoutEntitySession(request);
        }
        return countWithEntitySession + countWithoutEntitySession;
    }

    /**
     * @see ChatMessageService#saveChatMessage(ChatMessageSaveRequest)
     */
    @Override
    public long getChatSessionMessageCount(ChatSessionMessageCountRequest request) {
        return this.lambdaQueryWrapper()
            .eq(ChatMessageEntity::getChatSessionId, request.getChatSessionId())
            .gt(ChatMessageEntity::getCreatedAt, request.getClearAt())
            .gt(ChatMessageEntity::getId, request.getLastReadMessageId())
            .count();
    }

    @Override
    public Map<Long, Long> chatSessionMessageCountMap(List<ChatSessionMessageCountRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return Collections.emptyMap();
        }

        List<EntityLongBo> bos = super.mapper.chatSessionMessageCountBos(requests);
        return bos.stream().collect(
            Collectors.toMap(EntityLongBo::getId, EntityLongBo::getNumber, (e, _) -> e));
    }

    @Override
    public Page<EntityLongBo> findLastMessageBo(Collection<Long> bizIds, String bizType, Long userId, ChatPageRequest pageRequest) {
        if (CollectionUtils.isEmpty(bizIds)) {
            return Page.empty(pageRequest.getPageable());
        }
        IPage<EntityLongBo> iPage = this.mapper.findLastMessageBo(getMyBatisPlusPage(pageRequest), bizIds, bizType, userId, pageRequest);
        return MyBatisPlusUtils.toSpringDataPage(iPage);
    }

    @Override
    public List<EntityLongBo> lastMessageBoList(Collection<Long> chatSessionIds) {
        return super.mapper.lastMessageBoList(chatSessionIds);
    }

    @Override
    public Map<Long, ChatMessageVo> messageMap(Collection<Long> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return Collections.emptyMap();
        }
        List<ChatMessageEntity> entities = this.lambdaQueryWrapper()
            .in(ChatMessageEntity::getId, messageIds)
            .list();
        return entities.stream().collect(
            Collectors.toMap(ChatMessageEntity::getId, ChatMessageConverter.INSTANCE::entityToVo));
    }

    /**
     * @see ChatMessageService#getLastActiveTime(Long)
     */
    @Override
    public LocalDateTime getLastActiveTime(Long userId) {
        if (!ObjectUtils.isValidId(userId)) {
            return null;
        }
        ChatMessageEntity entity = this.findOneByWrapper(lambdaQueryWrapper()
            .eq(ChatMessageEntity::getSenderUserId, userId)
            .orderByDesc(ChatMessageEntity::getCreatedAt)
        );
        return Optional.ofNullable(entity).map(ChatMessageEntity::getCreatedAt).orElse(null);
    }

    /**
     * @see ChatMessageService#getLastActiveTime(Long)
     */
    @Override
    public Map<Long, LocalDateTime> getLastActiveTimeBatch(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<EntityDateBo> dateBos = super.mapper.getLastActiveTimeBatch(userIds);
        return dateBos.stream().collect(Collectors.toMap(EntityDateBo::getId, EntityDateBo::getDateTime));
    }

}
