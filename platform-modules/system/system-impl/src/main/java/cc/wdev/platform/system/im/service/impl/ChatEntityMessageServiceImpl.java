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
            .in(CollectionUtils.isNotEmpty(idList), ChatEntityMessageEntity::getId, idList)
            .list();
    }

}
