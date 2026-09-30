package cc.wdev.platform.system.message.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.message.domain.entity.MessageTemplateEntity;

import java.util.List;

/**
 * @author elvea
 */
public interface MessageTemplateService extends CachingEntityService<MessageTemplateEntity, Long> {

    /**
     * 获取单个消息通道实体
     *
     * @param messageType    消息类型
     * @param messageChannel 消息模板类型
     * @return 消息模板实体
     */
    MessageTemplateEntity getMessageTemplateEntity(String messageType, String messageChannel);

    /**
     * 获取消息通道下的消息模版列表
     */
    List<MessageTemplateEntity> findMessageTemplate(String messageType, List<String> messageChannelCodes);

}
