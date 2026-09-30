package cc.wdev.platform.system.im.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.im.domain.converter.ChatMessageContentConverter;
import cc.wdev.platform.system.im.domain.entity.ChatMessageContentEntity;
import cc.wdev.platform.system.im.domain.request.ChatMessageContentRequest;
import cc.wdev.platform.system.im.domain.request.ChatMessageContentSaveRequest;
import cc.wdev.platform.system.im.domain.vo.ChatMessageContentVo;
import cc.wdev.platform.system.im.repository.ChatMessageContentRepository;
import cc.wdev.platform.system.im.service.ChatMessageContentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Slf4j
@Service
public class ChatMessageContentServiceImpl extends BaseCachingEntityService<ChatMessageContentEntity, Long, ChatMessageContentRepository> implements ChatMessageContentService {

    @Override
    public Map<Long, ChatMessageContentVo> messageContentMap(Collection<Long> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return Collections.emptyMap();
        }
        List<ChatMessageContentEntity> entities = this.lambdaQueryWrapper()
            .in(ChatMessageContentEntity::getChatMessageId, messageIds)
            .list();
        return entities.stream()
            .collect(Collectors.toMap(
                ChatMessageContentEntity::getChatMessageId, ChatMessageContentConverter.INSTANCE::entityToVo));
    }

    /**
     * @see ChatMessageContentService#saveChatMessageContent(ChatMessageContentSaveRequest)
     */
    @Override
    public ChatMessageContentVo saveChatMessageContent(ChatMessageContentSaveRequest request) {
        ChatMessageContentEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(ChatMessageContentEntity::getChatMessageId, request.getChatMessageId()));
        if (entity == null) {
            entity = ChatMessageContentEntity.builder().build();
        }
        entity.setChatSessionId(request.getChatSessionId());
        entity.setChatMessageId(request.getChatMessageId());
        entity.setContent(request.getContent());
        entity.setExtra(request.getExtra());
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);

        return ChatMessageContentConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see ChatMessageContentService#saveBatchChatMessageContent(List)
     */
    @Override
    public void saveBatchChatMessageContent(List<ChatMessageContentSaveRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        List<Long> messageIds = requests.stream().map(ChatMessageContentSaveRequest::getChatMessageId).toList();
        List<ChatMessageContentEntity> exists = this.lambdaQueryWrapper()
            .in(ChatMessageContentEntity::getChatMessageId, messageIds)
            .list();
        Map<Long, ChatMessageContentEntity> existsMap = exists.stream().collect(
            Collectors.toMap(ChatMessageContentEntity::getChatMessageId, Function.identity(), (e, _) -> e));

        List<ChatMessageContentEntity> createList = Lists.newArrayList();
        List<ChatMessageContentEntity> updateList = Lists.newArrayList();
        for (ChatMessageContentSaveRequest request : requests) {
            ChatMessageContentEntity entity = existsMap.get(request.getChatMessageId());
            if (ObjectUtils.isValidId(entity)) {
                ChatMessageContentEntity update = new ChatMessageContentEntity();
                ChatMessageContentConverter.INSTANCE.saveReq2Entity(request, update);
                update.setId(entity.getId());
                update.setActive(ActiveTypeEnum.ENABLED.getValue());
                updateList.add(update);
                continue;
            }
            ChatMessageContentEntity create = new ChatMessageContentEntity();
            ChatMessageContentConverter.INSTANCE.saveReq2Entity(request, create);
            create.setActive(ActiveTypeEnum.ENABLED.getValue());
            createList.add(create);
        }
        if (CollectionUtils.isNotEmpty(createList)) {
            log.info("ChatMessageContent insert success. size={}", createList.size());
            this.insertBatch(createList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
            log.info("ChatMessageContent update success. size={}", updateList.size());
        }
    }

    /**
     * @see ChatMessageContentService#getChatMessageContent(ChatMessageContentRequest)
     */
    @Override
    public ChatMessageContentVo getChatMessageContent(ChatMessageContentRequest request) {
        ChatMessageContentEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(ChatMessageContentEntity::getChatMessageId, request.getChatMessageId()));
        return ChatMessageContentConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see ChatMessageContentService#deleteChatMessageContent(ChatMessageContentRequest)
     */
    @Override
    public void deleteChatMessageContent(ChatMessageContentRequest request) {
        ChatMessageContentEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(ChatMessageContentEntity::getChatMessageId, request.getChatMessageId()));
        this.softDelete(entity);
    }

}
