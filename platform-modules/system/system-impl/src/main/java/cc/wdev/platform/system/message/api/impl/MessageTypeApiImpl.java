package cc.wdev.platform.system.message.api.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.system.message.api.MessageTypeApi;
import cc.wdev.platform.system.message.domain.converter.MessageTemplateConverter;
import cc.wdev.platform.system.message.domain.converter.MessageTypeConverter;
import cc.wdev.platform.system.message.domain.entity.MessageChannelEntity;
import cc.wdev.platform.system.message.domain.entity.MessageTemplateEntity;
import cc.wdev.platform.system.message.domain.entity.MessageTypeEntity;
import cc.wdev.platform.system.message.domain.vo.MessageTemplateVo;
import cc.wdev.platform.system.message.domain.vo.MessageTypeVo;
import cc.wdev.platform.system.message.request.MessageChannelSearchRequest;
import cc.wdev.platform.system.message.request.MessageTypeRequest;
import cc.wdev.platform.system.message.request.MessageTypeSaveRequest;
import cc.wdev.platform.system.message.request.MessageTypeSearchRequest;
import cc.wdev.platform.system.message.service.MessageChannelService;
import cc.wdev.platform.system.message.service.MessageTemplateService;
import cc.wdev.platform.system.message.service.MessageTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.MESSAGE__MESSAGE_TYPE_NOT_PRESENT;
import static cc.wdev.platform.commons.utils.CollectionUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTypeApiImpl implements MessageTypeApi {

    private final MessageTypeService messageTypeService;

    private final MessageChannelService messageChannelService;

    private final MessageTemplateService messageTemplateService;

    /**
     * @see MessageTypeApi#findMessageType(PageRequest)
     */
    @Override
    public Page<MessageTypeVo> findMessageType(PageRequest request) {
        Page<MessageTypeEntity> page = this.messageTypeService.findByPage(request);

        List<MessageTypeVo> items = page.getContent().stream()
            .map(MessageTypeConverter.INSTANCE::entityToVo)
            .collect(Collectors.toList());

        return SpringDataUtils.toSpringDataPage(page, items);
    }

    @Override
    public List<MessageTypeVo> search(MessageTypeSearchRequest request) {
        List<MessageTypeEntity> entities = this.messageTypeService.search(request);
        return entities.stream()
            .map(MessageTypeConverter.INSTANCE::entityToVo)
            .collect(Collectors.toList());
    }

    /**
     * @see MessageTypeApi#getMessageType(MessageTypeRequest)
     */
    @Override
    public MessageTypeVo getMessageType(MessageTypeRequest request) {
        MessageTypeEntity entity = this.messageTypeService.findCacheById(request.getId());
        if (entity == null) {
            return null;
        }

        MessageTypeVo vo = MessageTypeConverter.INSTANCE.entityToVo(entity);
        if (request.isWithItem()) {
            if (entity.getId() != null && entity.getId() > 0) {
                // 获取当前可用的消息通道
                List<MessageChannelEntity> channelList = messageChannelService.search(MessageChannelSearchRequest.builder().build());
                List<String> messageChannelCodes = channelList.stream().map(MessageChannelEntity::getCode).toList();
                // 只查询可用通道对应的模板
                vo.setItems(messageTemplateService.findMessageTemplate(entity.getCode(), messageChannelCodes).stream()
                    .map(MessageTemplateConverter.INSTANCE::entityToVo)
                    .toList()
                );
            }
        }
        return vo;
    }

    /**
     * @see MessageTypeApi#saveMessageType(MessageTypeSaveRequest)
     */
    @Override
    public void saveMessageType(MessageTypeSaveRequest request) {
        MessageTypeEntity entity = this.messageTypeService.checkExistsOrFail(request.getId(), MESSAGE__MESSAGE_TYPE_NOT_PRESENT);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        this.messageTypeService.save(entity);

        List<MessageTemplateEntity> updateEntityList = Lists.newArrayList();
        for (MessageTemplateVo templateVo : nvl(request.getItems())) {
            MessageTemplateEntity templateEntity = messageTemplateService.getMessageTemplateEntity(templateVo.getMessageType(), templateVo.getMessageChannel());
            if (templateEntity != null) {
                templateEntity.setContent(templateVo.getContent());
                templateEntity.setStatus(templateVo.getStatus());
                updateEntityList.add(templateEntity);
            }
            this.messageTemplateService.saveBatch(updateEntityList);
        }
    }

}
