package cc.wdev.platform.system.message.api.impl;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.commons.utils.template.HtmlTemplateService;
import cc.wdev.platform.system.commons.constants.SystemRabbitConstants;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.domain.entity.UserEntity;
import cc.wdev.platform.system.core.service.TenantService;
import cc.wdev.platform.system.core.service.UserService;
import cc.wdev.platform.system.message.MessageSender;
import cc.wdev.platform.system.message.api.MessageApi;
import cc.wdev.platform.system.message.base.BaseMessageChannelEnum;
import cc.wdev.platform.system.message.base.BaseMessageTypeEnum;
import cc.wdev.platform.system.message.domain.dto.*;
import cc.wdev.platform.system.message.domain.entity.*;
import cc.wdev.platform.system.message.enums.MessageChannelEnum;
import cc.wdev.platform.system.message.enums.MessageStatusEnum;
import cc.wdev.platform.system.message.enums.MessageTargetTypeEnum;
import cc.wdev.platform.system.message.enums.MessageUserTypeEnum;
import cc.wdev.platform.system.message.sender.*;
import cc.wdev.platform.system.message.service.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.wdev.platform.commons.utils.ClassUtils.getEnumClass;
import static cc.wdev.platform.commons.utils.StringUtils.nvl;
import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;
import static cc.wdev.platform.system.commons.constants.SystemMessageConstants.TPL_CLASSPATH;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageApiImpl implements MessageApi {

    private final HtmlTemplateService htmlTemplateService;

    private final MessageTypeService messageTypeService;

    private final MessageChannelService messageChannelService;

    private final MessageService messageService;

    private final MessageUserService messageUserService;

    private final MessageContentService messageContentService;

    private final MessageTemplateService messageTemplateService;

    private final MessageRabbitService messageRabbitService;

    private final UserService userService;

    private final TenantService tenantService;

    // ------------------------------------------------------------------------
    // 消息通道相关
    // ------------------------------------------------------------------------

    /**
     * @see MessageApi#createMessage(CreateMessageDto)
     */
    @Override
    public Long createMessage(CreateMessageDto message) throws Exception {
        log.info("Create message. type - [{}]. start.", message.getType());

        // 获取消息类型
        MessageTypeEntity messageType = this.messageTypeService.findByCode(message.getType());
        if (null == messageType) {
            log.info("Create message. type - [{}]. Message type invalid.", message.getType());
            return 0L;
        }

        String messageTypeCode = messageType.getCode();
        if (StatusTypeEnum.ON.getValue().intValue() != messageType.getStatus().intValue()) {
            log.info("Create message. type - [{}]. Message type disabled.", messageTypeCode);
            return 0L;
        }

        // -------------------------------------------------------------------------------------------------------------
        // 保存消息记录
        // -------------------------------------------------------------------------------------------------------------

        MessageEntity entity = MessageEntity.builder()
            .messageType(messageTypeCode)
            .subject(StringUtils.isNotEmpty(message.getSubject()) ? message.getSubject() : messageType.getTitle())
            .content(message.getContent()).data(MapUtils.isNotEmpty(message.getParams()) ? JacksonUtils.toJson(message.getParams()) : "")
            .url(message.getUrl())
            .status(MessageStatusEnum.PENDING.getValue())
            .build();
        this.messageService.save(entity);

        Long messageId = entity.getId();

        log.info("Create message. type - [{}]. id - [{}]. created.", messageTypeCode, messageId);

        // -------------------------------------------------------------------------------------------------------------
        // 处理消息用户
        // -------------------------------------------------------------------------------------------------------------

        log.info("Create message. type - [{}]. id - [{}]. message user start.", messageTypeCode, messageId);

        List<MessageUserEntity> userEntityList = Lists.newArrayList();
        // 发件人
        userEntityList.add(createMessageUser(messageId, message.getSender()));
        // 收件人
        if (!CollectionUtils.isEmpty(message.getRecipients())) {
            for (MessageUserDto user : message.getRecipients()) {
                userEntityList.add(createMessageUser(messageId, user));
            }
        }
        this.messageUserService.saveBatch(userEntityList);

        log.info("Create message. type - [{}]. id - [{}]. message user done.", messageTypeCode, messageId);

        // -------------------------------------------------------------------------------------------------------------
        // 处理消息内容
        // 如果指定了模版，那将沿用指定的模版来发送消息
        // 未指定的情况，则用系统已经保存并启用的模版来发送
        // -------------------------------------------------------------------------------------------------------------

        log.info("Create message. type - [{}]. id - [{}]. message content start.", messageTypeCode, messageId);

        List<MessageChannelEntity> messageChannelList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(message.getTemplateTypeList())) {
            for (BaseMessageChannelEnum templateTypeCode : message.getTemplateTypeList()) {
                MessageChannelEntity messageChannelEntity = this.messageChannelService.findByCode(templateTypeCode.getValue());
                if (null != messageChannelEntity) {
                    messageChannelList.add(messageChannelEntity);
                }
            }
        } else {
            messageChannelList.addAll(this.messageChannelService.findAll());
        }

        List<MessageContentEntity> contentEntityList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(messageChannelList)) {
            for (MessageChannelEntity messageChannel : messageChannelList) {
                String messageChannelCode = messageChannel.getCode();

                log.info("Create message. type - [{}]. template - [{}]. id - [{}]. message content start.", messageTypeCode, messageChannelCode, messageId);

                if (StatusTypeEnum.ON.getValue().intValue() != messageChannel.getStatus().intValue()) {
                    log.info("Create message. type - [{}]. template - [{}]. id - [{}]. channel disabled.", messageTypeCode, messageChannelCode, messageId);
                    continue;
                }

                String content;
                if (StringUtils.isNotEmpty(message.getContent())) {
                    log.info("Create message. type - [{}]. template - [{}]. id - [{}]. use content.", messageTypeCode, messageChannelCode, messageId);
                    content = message.getContent();
                } else if (StringUtils.isNotEmpty(message.getTemplate())) {
                    log.info("Create message. type - [{}]. template - [{}]. id - [{}]. use template.", messageTypeCode, messageChannelCode, messageId);
                    content = htmlTemplateService.toHtml(message.getTemplate(), message.getParams());
                } else {
                    log.info("Create message. type - [{}]. template - [{}]. id - [{}]. use system template.", messageTypeCode, messageChannelCode, messageId);

                    MessageTemplateEntity messageTemplateEntity = this.messageTemplateService.getMessageTemplateEntity(messageTypeCode, messageChannelCode);
                    // 检查消息模版是否存在并处于正常开启状态
                    if (messageTemplateEntity == null) {
                        log.info("Create message. type - [{}]. template - [{}]. id - [{}]. system template invalid.", messageTypeCode, messageChannelCode, messageId);
                        continue;
                    }
                    if (StatusTypeEnum.ON.getValue().intValue() != messageTemplateEntity.getStatus().intValue()) {
                        log.info("Create message. type - [{}]. template - [{}]. id - [{}]. system template disabled.", messageTypeCode, messageChannelCode, messageId);
                        continue;
                    }
                    content = htmlTemplateService.toHtml(messageTemplateEntity.getContent(), message.getParams());
                }

                contentEntityList.add(MessageContentEntity.builder()
                    .messageChannel(messageChannelCode)
                    .messageType(messageTypeCode)
                    .messageId(entity.getId())
                    .content(content)
                    .build()
                );
            }

            if (CollectionUtils.isNotEmpty(contentEntityList)) {
                this.messageContentService.saveBatch(contentEntityList);
            } else {
                log.info("Create message. type - [{}]. id - [{}]. message content empty.", messageTypeCode, messageId);
            }
            log.info("Create message. type - [{}]. id - [{}]. message content done.", messageTypeCode, messageId);
        }

        log.info("Create message. type - [{}]. id - [{}]. done.", messageTypeCode, messageId);

        // 如果是立刻发送，消息创建完成后，直接推到消息队列
        if (MessageTargetTypeEnum.IMMEDIATE.equals(message.getTargetType())) {
            log.info("Create message. type - [{}]. id - [{}]. send to message queue.", messageTypeCode, messageId);
            this.messageRabbitService.send(SystemRabbitConstants.MESSAGE_QUEUE, SendMessageAmqpDto.builder().id(messageId).build());
        }

        return messageId;
    }

    /**
     * @see MessageApi#sendMessage()
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void sendMessage() {
        log.info("Send message. start.");

        List<MessageEntity> messageEntityList = this.messageService.findByStatus(Collections.singletonList(MessageStatusEnum.PENDING));
        if (CollectionUtils.isNotEmpty(messageEntityList)) {
            log.info("Send message. {} messages found.", messageEntityList.size());

            for (MessageEntity messageEntity : messageEntityList) {
                try {
                    log.info("Send message. id - [{}]. start.", messageEntity.getId());

                    // 修改消息发送状态为发送中
                    messageEntity.setStatus(MessageStatusEnum.SENDING.getValue());
                    this.messageService.save(messageEntity);

                    // 推送到消息队列
                    this.messageRabbitService.send(SystemRabbitConstants.MESSAGE_QUEUE, SendMessageAmqpDto.builder().id(messageEntity.getId()).build());

                    log.info("Send message. id - [{}]. done.", messageEntity.getId());
                } catch (Exception e) {
                    log.error("Send message. id - [{}]. failed.", messageEntity.getId(), e);
                }
            }
        } else {
            log.info("Send message. no messages found.");
        }
        log.info("Send message. done.");
    }

    /**
     * @see MessageApi#sendMessage(Long)
     */
    @Override
    public void sendMessage(Long messageId) throws Exception {
        this.sendMessage(messageId, false);
    }

    /**
     * @see MessageApi#sendMessage(Long, boolean)
     */
    @Override
    public void sendMessage(Long messageId, boolean force) throws Exception {
        log.info("Send message. id - [{}]. force [{}]. start.", messageId, force);

        // 查询消息记录
        MessageEntity messageEntity = this.messageService.findById(messageId);
        if (messageEntity == null) {
            log.info("Send message. id - [{}]. message invalid.", messageId);
            return;
        }

        try {
            // 设置租户上下文
            TenantContext.setTenantId(messageEntity.getTenantId());
            log.info("Send message. tenantId - [{}]. messageId - [{}]. start.", TenantContext.getTenantId(), messageId);

            // 查询消息类型
            // 1. 找不到类型记录，跳过
            // 2. 消息类型记录如果是未启用状态或者未发布，跳过
            MessageTypeEntity messageType = this.messageTypeService.findByCode(messageEntity.getMessageType());
            if (null == messageType) {
                log.info("Send message. id - [{}]. Message type invalid.", messageId);
                return;
            }

            String messageTypeCode = messageType.getCode();
            if (StatusTypeEnum.ON.getValue().intValue() != messageType.getStatus().intValue()) {
                log.info("Send message. type - [{}]. id - [{}]. Message type disabled.", messageTypeCode, messageId);
                return;
            }

            // 查询消息用户记录
            // 1. 找不到类型记录，跳过
            // 2. 消息类型记录如果是未启用状态或者未发布，那跳过不发送消息，
            List<MessageUserEntity> messageUserEntityList = this.messageUserService.findByMessage(messageId);
            if (CollectionUtils.isEmpty(messageUserEntityList)) {
                log.info("Send message. type - [{}]. id - [{}]. No message user found.", messageTypeCode, messageId);
                return;
            }

            List<MessageUserEntity> sender = Lists.newArrayList();
            List<MessageUserEntity> recipients = Lists.newArrayList();
            for (MessageUserEntity messageUserEntity : messageUserEntityList) {
                MessageUserTypeEnum messageUserTypeEnum = BaseEnum.getEnumByValue(messageUserEntity.getUserType(), MessageUserTypeEnum.class);
                if (MessageUserTypeEnum.FROM.equals(messageUserTypeEnum)) {
                    sender.add(messageUserEntity);
                } else if (MessageUserTypeEnum.TO.equals(messageUserTypeEnum)) {
                    recipients.add(messageUserEntity);
                }
            }
            if (CollectionUtils.isEmpty(sender)) {
                log.info("Send message. type - [{}]. id - [{}]. No message sender found.", messageTypeCode, messageId);
                return;
            }
            if (CollectionUtils.isEmpty(recipients)) {
                log.info("Send message. type - [{}]. id - [{}]. No message recipient found.", messageTypeCode, messageId);
                return;
            }

            // 查询消息内容记录
            List<MessageContentEntity> messageContentEntityList = this.messageContentService.findByMessage(messageId);
            if (CollectionUtils.isEmpty(messageContentEntityList)) {
                log.info("Send message. type - [{}]. id - [{}]. No message content found.", messageTypeCode, messageId);
                return;
            }
            for (MessageContentEntity messageContentEntity : messageContentEntityList) {
                String messageChannelCode = messageContentEntity.getMessageChannel();

                log.info("Send message. type - [{}]. template - [{}]. id - [{}]. message content start.", messageTypeCode, messageChannelCode, messageId);

                MessageTemplateEntity messageTemplateEntity = this.messageTemplateService.getMessageTemplateEntity(messageTypeCode, messageChannelCode);
                if (messageTemplateEntity == null) {
                    log.info("Send message. type - [{}]. template - [{}]. id - [{}]. channel invalid.", messageTypeCode, messageChannelCode, messageId);
                    continue;
                }

                MessageChannelEnum messageChannelEnum = BaseEnum.getEnumByValue(messageChannelCode, MessageChannelEnum.class);
                MessageSender messageSender = getMessageSender(messageChannelEnum);
                if (messageSender == null) {
                    log.info("Send message. type - [{}]. template - [{}]. id - [{}]. message sender invalid.", messageTypeCode, messageChannelCode, messageId);
                    continue;
                }

                Map<String, Object> params = new HashMap<>();
                try {
                    if (StringUtils.isNotEmpty(messageEntity.getData())) {
                        params.putAll(JacksonUtils.toMap(messageEntity.getData()));
                    }
                } catch (Exception e) {
                    log.info("Send message. type - [{}]. template - [{}]. id - [{}]. message data invalid.", messageTypeCode, messageChannelCode, messageId);
                    continue;
                }

                for (MessageUserEntity senderEntity : sender) {
                    List<MessageRecipientDto> recipientList = Lists.newArrayListWithCapacity(recipients.size());
                    for (MessageUserEntity recipientEntity : recipients) {
                        MessageRecipientDto recipient = this.messageUserService.getRecipient(recipientEntity);
                        recipientList.add(recipient);
                    }

                    SendMessageDto sendMessageDto = SendMessageDto.builder()
                        .id(messageEntity.getId())
                        .tenantId(messageEntity.getTenantId())
                        .contentId(messageContentEntity.getId())
                        .subject(messageEntity.getSubject())
                        .params(params)
                        .url(messageEntity.getUrl())
                        .content(messageContentEntity.getContent())
                        .sender(this.messageUserService.getSender(senderEntity))
                        .recipients(recipientList)
                        .build();

                    messageSender.send(sendMessageDto);
                }

                // 更新消息的发送状态和发送时间
                messageEntity.setSentDatetime(this.messageService.getCurLocalDateTime());
                messageEntity.setStatus(MessageStatusEnum.SENT.getValue());
                this.messageService.save(messageEntity);
            }
            log.info("Send message [{}]. done.", messageId);
        } finally {
            log.info("Send message. tenantId - [{}]. messageId - [{}]. done.", TenantContext.getTenantId(), messageId);
            TenantContext.clear();
        }
    }

    // ------------------------------------------------------------------------
    // 数据初始化
    // ------------------------------------------------------------------------

    /**
     * @see MessageApi#initialize()
     */
    @Override
    public void initialize() {
        // 初始化消息通道
        this.initializeMessageChannel();
        // 初始化消息类型
        this.initializeMessageType();
        // 初始化消息模板
        this.initializeMessageTemplate(true);
    }

    /**
     * @see MessageApi#initializeMessageChannel()
     */
    @Override
    public void initializeMessageChannel() {
        log.info("Initialize message channel start.");

        List<BaseMessageChannelEnum> enumList = getEnumClass(GLOABL_BASE_PACKAGE, BaseMessageChannelEnum.class);

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize message channel skip. no tenant.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            try {
                log.info("Initialize tenant [{}] message channel start.", tenant.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                List<MessageChannelEntity> updateEntityList = Lists.newArrayList();
                List<MessageChannelEntity> insertEntityList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(enumList)) {
                    for (BaseMessageChannelEnum messageChannelEnum : enumList) {
                        MessageChannelEntity entity = this.messageChannelService.findByCode(messageChannelEnum.getValue());
                        if (entity != null) {
                            updateEntityList.add(entity);
                        } else {
                            entity = new MessageChannelEntity();
                            insertEntityList.add(entity);
                        }
                        entity.setCode(messageChannelEnum.getValue());
                        entity.setLabel(messageChannelEnum.getLabelKey());
                        entity.setTitle(messageChannelEnum.getTitle());
                        entity.setTemplateType(messageChannelEnum.getTemplateType());
                        entity.setDescription(messageChannelEnum.getDescription());
                        entity.setStatus(StatusTypeEnum.ON.getValue());
                        entity.setActive(Boolean.TRUE.equals(messageChannelEnum.getEnabled()) ? ActiveTypeEnum.ENABLED.getValue() : ActiveTypeEnum.DISABLED.getValue());
                    }
                    this.messageChannelService.insertBatch(insertEntityList);
                    this.messageChannelService.updateBatchById(updateEntityList);
                }

                log.info("Initialize tenant [{}] message channel done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }

        // 清空缓存
        log.info("Initialize message channel cache.");
        this.messageChannelService.clearCache();

        log.info("Initialize message channel done.");
    }

    /**
     * @see MessageApi#initializeMessageType ()
     */
    @Override
    public void initializeMessageType() {
        log.info("Initialize message type start.");

        // 消息类型
        List<BaseMessageTypeEnum> enumList = getEnumClass(GLOABL_BASE_PACKAGE, BaseMessageTypeEnum.class);
        if (CollectionUtils.isEmpty(enumList)) {
            log.info("Initialize message type skip. no enum.");
            return;
        }

        // 平台范围消息类型
        List<BaseMessageTypeEnum> platformEnumList = enumList.stream()
            .filter((e) -> BizScopeTypeEnum.PLATFORM.getCode().equals(e.getScope()) || BizScopeTypeEnum.SYSTEM.getCode().equals(e.getScope()))
            .toList();

        // 系统范围消息类型
        List<BaseMessageTypeEnum> systemEnumList = enumList.stream()
            .filter((e) -> BizScopeTypeEnum.SYSTEM.getCode().equals(e.getScope()))
            .toList();

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize message type skip. no tenant.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            try {
                log.info("Initialize tenant [{}] message type start.", tenant.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                // 这里如果非顶层租户需要排除可用的消息类型
                List<BaseMessageTypeEnum> tenantEnumList = Lists.newArrayList();
                if (BooleanTypeEnum.isTrueValue(tenant.getRootInd())) {
                    tenantEnumList.addAll(platformEnumList);
                } else {
                    tenantEnumList.addAll(systemEnumList);
                }

                List<MessageTypeEntity> updateEntityList = Lists.newArrayList();
                List<MessageTypeEntity> insertEntityList = Lists.newArrayList();
                for (BaseMessageTypeEnum typeEnum : tenantEnumList) {
                    MessageTypeEntity entity = this.messageTypeService.findByCode(typeEnum.getValue());
                    if (entity != null) {
                        updateEntityList.add(entity);
                    } else {
                        entity = new MessageTypeEntity();
                        insertEntityList.add(entity);
                    }
                    entity.setCode(typeEnum.getValue());
                    entity.setTitle(typeEnum.getTitle());
                    entity.setLabel(typeEnum.getLabelKey());
                    entity.setDescription(typeEnum.getDescription());
                    entity.setStatus(StatusTypeEnum.ON.getValue());
                    entity.setActive(ActiveTypeEnum.ENABLED.getValue());
                }
                this.messageTypeService.insertBatch(insertEntityList);
                this.messageTypeService.updateBatchById(updateEntityList);

                log.info("Initialize tenant [{}] message type done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }

        // 清空缓存
        log.info("Initialize message type cache.");
        this.messageTypeService.clearCache();

        log.info("Initialize message type done.");
    }

    /**
     * @see MessageApi#initializeMessageTemplate(boolean)
     */
    @Override
    public void initializeMessageTemplate(boolean force) {
        log.info("Initialize message template start.");

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize message template skip. no tenant.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            try {
                log.info("Initialize tenant [{}] message template start.", tenant.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                // 查询当前所有消息通道
                List<MessageChannelEntity> messageChannelList = this.messageChannelService.findAll();
                if (CollectionUtils.isEmpty(messageChannelList)) {
                    log.info("Initialize message template. no message channel found.");
                    return;
                }

                // 查询当前所有消息类型
                List<MessageTypeEntity> messageTypeList = this.messageTypeService.findAll();
                if (CollectionUtils.isEmpty(messageTypeList)) {
                    log.info("Initialize message template. no message type found.");
                    return;
                }

                // 查询当前所有消息模版
                Map<String, MessageTemplateEntity> messageTemplateMap = new HashMap<>();
                List<MessageTemplateEntity> messageTemplateList = this.messageTemplateService.findAll();
                if (CollectionUtils.isNotEmpty(messageTemplateList)) {
                    messageTemplateList.forEach(item -> messageTemplateMap.put(item.getMessageType() + "__" + item.getMessageChannel(), item));
                }

                // 遍历当前所有的消息类型，逐一刷新消息内容模版
                String messageTypeCode, messageChannelCode;
                List<MessageTemplateEntity> updateEntityList = Lists.newArrayList(), insertEntityList = Lists.newArrayList();
                for (MessageTypeEntity messageTypeEntity : messageTypeList) {
                    messageTypeCode = messageTypeEntity.getCode();

                    log.info("Sync message template. type - [{}]. start.", messageTypeCode);

                    for (MessageChannelEntity messageChannelEntity : messageChannelList) {
                        messageChannelCode = messageChannelEntity.getCode();

                        log.info("Sync message template. type - [{}]. channel - [{}]. start.", messageTypeCode, messageChannelCode);

                        MessageTemplateEntity messageTemplateEntity = messageTemplateMap.get(messageTypeCode + "__" + messageChannelCode);
                        if (messageTemplateEntity == null) {
                            // 当数据库不存在消息模版时，创建消息模版
                            log.info("Sync message template. type - [{}]. channel - [{}]. create message template.", messageTypeCode, messageChannelCode);

                            messageTemplateEntity = new MessageTemplateEntity();
                            messageTemplateEntity.setMessageChannel(messageChannelCode);
                            messageTemplateEntity.setMessageType(messageTypeCode);
                            messageTemplateEntity.setContent(this.getMessageTemplateContent(messageTypeCode, messageChannelCode));

                            insertEntityList.add(messageTemplateEntity);
                        } else if (StringUtils.isEmpty(messageTemplateEntity.getContent()) || force) {
                            // 当强制刷新或者模版内容为空时，读取模版文件，更新到数据库

                            log.info("Sync message template. type - [{}]. channel - [{}]. update message template.", messageTypeCode, messageChannelCode);

                            messageTemplateEntity.setContent(this.getMessageTemplateContent(messageTypeCode, messageChannelCode));

                            updateEntityList.add(messageTemplateEntity);
                        } else {
                            log.info("Sync message template. type - [{}]. channel - [{}]. skip.", messageTypeCode, messageChannelCode);
                        }
                    }
                }

                // 保存消息模版
                this.messageTemplateService.updateBatchById(updateEntityList);
                this.messageTemplateService.insertBatch(insertEntityList);

                log.info("Initialize tenant [{}] message template done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }

        // 刷新缓存
        this.messageChannelService.clearCache();
        this.messageTypeService.clearCache();
        this.messageTemplateService.clearCache();

        log.info("Initialize message template. done.");
    }

    // ------------------------------------------------------------------------
    // 私有辅助函数
    // ------------------------------------------------------------------------

    private String getMessageTemplateContent(String messageTypeCode, String messageChannelCode) {
        String content = null;
        try {
            String path = (TPL_CLASSPATH + messageTypeCode.toLowerCase() + "/" + messageChannelCode.toLowerCase() + ".html").toLowerCase();
            ClassPathResource classPathResource = new ClassPathResource(path);
            content = IOUtils.toString(classPathResource.getInputStream(), GlobalConstants.ENCODING);
        } catch (Exception e) {
            log.warn("Failed to get message template content. type - [{}]. channel - [{}].", messageTypeCode, messageChannelCode);
        }
        return nvl(content).trim();
    }

    /**
     * 辅助方法，用于获取消息发送器
     */
    private MessageSender getMessageSender(MessageChannelEnum messageChannelEnum) {
        if (messageChannelEnum == null) {
            return null;
        }
        return switch (messageChannelEnum) {
            case NOTICE -> SpringUtils.getBean(MessageNoticeSender.class);
            case SMS -> SpringUtils.getBean(MessageSmsSender.class);
            case MAIL -> SpringUtils.getBean(MessageMailSender.class);
            case WX_MP -> SpringUtils.getBean(MessageWxMpSender.class);
            case WX_CP -> SpringUtils.getBean(MessageWxCpSender.class);
            case LARK -> SpringUtils.getBean(MessageLarkSender.class);
            case DINGTALK -> SpringUtils.getBean(MessageDingTalkSender.class);
        };
    }

    /**
     * 辅助方法，用于获取系统用户的邮箱和手机号码等信息
     */
    private MessageUserEntity createMessageUser(Long messageId, MessageUserDto userDto) {
        MessageUserEntity.MessageUserEntityBuilder builder = MessageUserEntity.builder()
            .messageId(messageId)
            .userType(userDto.getType().getValue())
            .userId((ObjectUtils.isEmpty(userDto.getUserId()) || userDto.getUserId() <= 0) ? 0L : userDto.getUserId());

        UserEntity userEntity = null;
        if (!ObjectUtils.isEmpty(userDto.getUserId()) && userDto.getUserId() > 0) {
            userEntity = this.userService.findCacheById(userDto.getUserId());
        }

        if (userEntity != null) {
            builder.username(StringUtils.isNotEmpty(userDto.getUsername()) ? userDto.getUsername() : userEntity.getUsername());
            builder.email(StringUtils.isNotEmpty(userDto.getEmail()) ? userDto.getEmail() : userEntity.getEmail());
            builder.mobileCountryCode(StringUtils.isNotEmpty(userDto.getMobileCountryCode()) ? userDto.getMobileCountryCode() : userEntity.getMobileCountryCode());
            builder.mobileNumber(StringUtils.isNotEmpty(userDto.getMobileNumber()) ? userDto.getMobileNumber() : userEntity.getMobileNumber());
        } else {
            builder.username(userDto.getUsername());
            builder.email(userDto.getEmail());
            builder.mobileCountryCode(userDto.getMobileCountryCode());
            builder.mobileNumber(userDto.getMobileNumber());
        }
        return builder.build();
    }

}
