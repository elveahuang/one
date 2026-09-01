package cc.wdev.platform.system.message.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.message.domain.entity.MessageContentEntity;

import java.util.List;

/**
 * @author elvea
 */
public interface MessageContentService extends EntityService<MessageContentEntity, Long> {

    List<MessageContentEntity> findByMessage(Long messageId);

    void success(Long id, String resp);

    void fail(Long id, String resp);

    void fail(Long id, String resp, String exception);

}
