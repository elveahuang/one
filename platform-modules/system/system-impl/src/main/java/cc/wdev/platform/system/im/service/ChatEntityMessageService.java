package cc.wdev.platform.system.im.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.im.domain.entity.ChatEntityMessageEntity;
import cc.wdev.platform.system.im.domain.request.ChatEntityMessageRequest;

import java.util.List;

/**
 * @author elvea
 */
public interface ChatEntityMessageService extends CachingEntityService<ChatEntityMessageEntity, Long> {

    /**
     * 获取实体消息
     */
    List<ChatEntityMessageEntity> findChatEntityMessage(ChatEntityMessageRequest request);

}
