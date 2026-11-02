package cc.wdev.platform.system.im.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.im.domain.entity.ChatEntityMessageEntity;
import cc.wdev.platform.system.im.domain.request.ChatEntityMessageRequest;
import cc.wdev.platform.system.im.repository.ChatEntityMessageRepository;
import cc.wdev.platform.system.im.service.ChatEntityMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
public class ChatEntityMessageServiceImpl
    extends BaseCachingEntityService<ChatEntityMessageEntity, Long, ChatEntityMessageRepository>
    implements ChatEntityMessageService {

    /**
     * @see ChatEntityMessageService#findChatEntityMessage(ChatEntityMessageRequest)
     */
    @Override
    public List<ChatEntityMessageEntity> findChatEntityMessage(ChatEntityMessageRequest request) {
        List<Long> idList = request.getChatMessageIdList();

        return this.lambdaQueryWrapper()
            .eq(ChatEntityMessageEntity::getChatSessionId, request.getChatSessionId())
            .eq(ChatEntityMessageEntity::getUserId, request.getUserId())
            // 入参是业务消息 ID，必须匹配 chat_message_id（此前误用主键 id，导致永远查不到、反复插入重复行）
            .in(CollectionUtils.isNotEmpty(idList), ChatEntityMessageEntity::getChatMessageId, idList)
            .list();
    }

}
