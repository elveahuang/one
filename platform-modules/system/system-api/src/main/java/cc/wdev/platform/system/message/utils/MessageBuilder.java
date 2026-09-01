package cc.wdev.platform.system.message.utils;

import cc.wdev.platform.system.message.domain.dto.CreateMessageDto;
import cc.wdev.platform.system.message.domain.dto.MessageUserDto;
import cc.wdev.platform.system.message.enums.MessageChannelEnum;
import cc.wdev.platform.system.message.enums.MessageTargetTypeEnum;
import cc.wdev.platform.system.message.enums.MessageUserTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public class MessageBuilder {
    /**
     * 消息标题
     */
    private String subject;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息链接url
     */
    private String url;
    /**
     * 消息模版
     */
    private String template;
    /**
     * 消息类型
     */
    private String type;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 发件人
     */
    private MessageUserDto sender;
    /**
     * 收件人
     */
    private final List<MessageUserDto> recipients = Lists.newArrayList();
    /**
     * 指定模版
     */
    private final List<MessageChannelEnum> templateTypeList = Lists.newArrayList();
    /**
     * 发送方式
     */
    private MessageTargetTypeEnum targetType;
    /**
     * 目标发送时间
     */
    private LocalDateTime targetSentDatetime;
    /**
     * 参数
     */
    private Map<String, Object> params = Maps.newHashMap();

    private MessageBuilder() {
    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public MessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageBuilder content(String content) {
        this.content = content;
        return this;
    }

    public MessageBuilder url(String url) {
        this.url = url;
        return this;
    }

    public MessageBuilder template(String template) {
        this.template = template;
        return this;
    }

    public MessageBuilder type(String type) {
        this.type = type;
        return this;
    }

    public MessageBuilder bizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public MessageBuilder sender(Long userId) {
        this.sender = new MessageUserDto(MessageUserTypeEnum.FROM, userId);
        return this;
    }

    public MessageBuilder recipient(Long userId) {
        this.recipients.add(new MessageUserDto(MessageUserTypeEnum.TO, userId));
        return this;
    }

    public MessageBuilder recipient(List<Long> recipientIds) {
        if (!CollectionUtils.isEmpty(recipientIds)) {
            for (Long userId : recipientIds) {
                this.recipients.add(new MessageUserDto(MessageUserTypeEnum.TO, userId));
            }
        }
        return this;
    }

    public MessageBuilder recipient(String email) {
        this.recipients.add(new MessageUserDto(MessageUserTypeEnum.TO, email));
        return this;
    }

    public MessageBuilder recipients(List<String> emailList) {
        if (!CollectionUtils.isEmpty(emailList)) {
            emailList.forEach((email -> this.recipients.add(new MessageUserDto(MessageUserTypeEnum.TO, email))));
        }
        return this;
    }

    public MessageBuilder recipient(String mobileCountryCode, String mobileNumber) {
        this.recipients.add(new MessageUserDto(MessageUserTypeEnum.TO, mobileCountryCode, mobileNumber));
        return this;
    }

    public MessageBuilder recipientByUsername(String username) {
        this.recipients.add(MessageUserDto.builder().type(MessageUserTypeEnum.TO).username(username).build());
        return this;
    }

    public MessageBuilder templateType(MessageChannelEnum templateType) {
        this.templateTypeList.add(templateType);
        return this;
    }

    public MessageBuilder targetType(MessageTargetTypeEnum targetType) {
        this.targetType = targetType;
        return this;
    }

    public MessageBuilder targetSentDatetime(LocalDateTime targetSentDatetime) {
        this.targetSentDatetime = targetSentDatetime;
        return this;
    }

    public MessageBuilder params(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public CreateMessageDto build() {
        CreateMessageDto message = new CreateMessageDto();
        message.setSubject(this.subject);
        message.setContent(this.content);
        message.setUrl(this.url);
        message.setTemplate(this.template);
        message.setBizId(this.bizId);
        message.setType(this.type);
        message.setTargetSentDatetime(this.targetSentDatetime);
        message.setSender(this.sender);
        message.setRecipients(this.recipients);
        message.setTemplateTypeList(this.templateTypeList);
        message.setParams(this.params);
        message.setTargetType((this.targetType == null) ? MessageTargetTypeEnum.AUTO : MessageTargetTypeEnum.IMMEDIATE);
        return message;
    }

}
