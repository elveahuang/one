package cc.wdev.platform.system.message.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.message.domain.entity.MessageTypeEntity;
import cc.wdev.platform.system.message.request.MessageTypeSearchRequest;

import java.util.List;

/**
 * @author elvea
 */
public interface MessageTypeService extends CachingEntityService<MessageTypeEntity, Long> {

    List<MessageTypeEntity> search(MessageTypeSearchRequest request);
}
