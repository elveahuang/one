package cc.wdev.platform.system.message.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.message.domain.entity.MessageChannelEntity;
import cc.wdev.platform.system.message.request.MessageChannelSearchRequest;

import java.util.List;

/**
 * @author elvea
 */
public interface MessageChannelService extends CachingEntityService<MessageChannelEntity, Long> {

    List<MessageChannelEntity> search(MessageChannelSearchRequest request);

}
