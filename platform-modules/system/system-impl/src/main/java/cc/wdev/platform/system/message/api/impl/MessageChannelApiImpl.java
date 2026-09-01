package cc.wdev.platform.system.message.api.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.system.message.api.MessageChannelApi;
import cc.wdev.platform.system.message.domain.converter.MessageChannelConverter;
import cc.wdev.platform.system.message.domain.entity.MessageChannelEntity;
import cc.wdev.platform.system.message.domain.entity.MessageTemplateEntity;
import cc.wdev.platform.system.message.domain.vo.MessageChannelVo;
import cc.wdev.platform.system.message.request.MessageChannelRequest;
import cc.wdev.platform.system.message.request.MessageChannelSaveRequest;
import cc.wdev.platform.system.message.request.MessageChannelSearchRequest;
import cc.wdev.platform.system.message.service.MessageChannelService;
import cc.wdev.platform.system.message.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageChannelApiImpl implements MessageChannelApi {

    private final MessageChannelService messageChannelService;

    private final MessageTemplateService messageTemplateService;

    /**
     * @see MessageChannelApi#findMessageChannel(PageRequest)
     */
    @Override
    public Page<MessageChannelVo> findMessageChannel(PageRequest request) {
        Page<MessageChannelEntity> page = this.messageChannelService.findByPage(request);

        List<MessageChannelVo> items = page.getContent().stream()
            .map(MessageChannelConverter.INSTANCE::entityToVo)
            .collect(Collectors.toList());
        return SpringDataUtils.toSpringDataPage(page, items);
    }

    @Override
    public List<MessageChannelVo> search(MessageChannelSearchRequest request) {
        List<MessageTemplateEntity> templates = messageTemplateService.findMessageTemplate(request.getMessageType(), null);
        if (CollectionUtils.isNotEmpty(templates)) {
            List<String> codeList = templates.stream()
                .filter(template -> Objects.equals(StatusTypeEnum.ON.getValue(), template.getStatus()))
                .map(MessageTemplateEntity::getMessageChannel)
                .toList();
            request.setCodeList(codeList);
            if (CollectionUtils.isEmpty(codeList)) {
                return Collections.emptyList();
            }
        }

        List<MessageChannelEntity> entities = this.messageChannelService.search(request);
        return entities.stream()
            .map(MessageChannelConverter.INSTANCE::entityToVo)
            .collect(Collectors.toList());
    }

    /**
     * @see MessageChannelApi#getMessageChannel(MessageChannelRequest)
     */
    @Override
    public MessageChannelVo getMessageChannel(MessageChannelRequest request) {
        MessageChannelEntity entity = this.messageChannelService.findCacheById(request.getId());
        if (entity == null) {
            return null;
        }
        return MessageChannelConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see MessageChannelApi#saveMessageChannel(MessageChannelSaveRequest)
     */
    @Override
    public void saveMessageChannel(MessageChannelSaveRequest request) {
        MessageChannelEntity entity = this.messageChannelService.findCacheById(request.getId());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        this.messageChannelService.save(entity);
    }

}
