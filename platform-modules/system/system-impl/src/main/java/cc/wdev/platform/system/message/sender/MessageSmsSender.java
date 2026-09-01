package cc.wdev.platform.system.message.sender;

import cc.wdev.platform.commons.oapis.sms.*;
import cc.wdev.platform.commons.oapis.sms.enums.SmsTypeEnum;
import cc.wdev.platform.commons.utils.ExceptionUtils;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.enums.ConfigBizTypeEnum;
import cc.wdev.platform.system.message.MessageSender;
import cc.wdev.platform.system.message.domain.dto.MessageRecipientDto;
import cc.wdev.platform.system.message.domain.dto.SendMessageDto;
import cc.wdev.platform.system.message.service.MessageContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Slf4j
@Service
public class MessageSmsSender implements MessageSender {

    private ConfigApi configApi;

    private SmsFactory smsFactory;

    private MessageContentService messageContentService;

    @Override
    public void send(SendMessageDto message) {
        // 检查短信服务是否已经启动
        if (this.smsFactory == null) {
            log.info("Send sms message. message id [{}]. message content id [{}]. failed. sms is disabled.", message.getId(), message.getContentId());
            return;
        }

        // 是否跳过验证码检查，只能用于本地开发调试
        if (this.configApi.getBoolean(ConfigBizTypeEnum.DEV_PASS_CAPTCHA.getCode(), false)) {
            return;
        }

        // 获取短信服务配置
        SmsConfig config = this.configApi.getSmsConfig();

        for (MessageRecipientDto recipient : message.getRecipients()) {
            try {
                log.info("Send sms message. message id [{}]. message content id [{}]. start.", message.getId(), message.getContentId());

                if (SmsTypeEnum.None.equals(config.getType())) {
                    log.info("Send sms message. message id [{}]. message content id [{}]. failed. sms server is disabled.", message.getId(), message.getContentId());
                    this.messageContentService.fail(message.getContentId(), "Sms server is disabled", "");
                    continue;
                }

                SmsSender<?, SmsResult> sender = smsFactory.getSmsSender(this.configApi.getSmsConfig());
                if (null != sender) {
                    SmsResult result = sender.send(SmsBody.builder()
                        .mobileCountryCode(recipient.getMobileCountryCode())
                        .mobileNumber(recipient.getMobileNumber())
                        .template(message.getContent())
                        .params(message.getParams())
                        .build()
                    );

                    this.messageContentService.success(message.getContentId(), result.getResponse());
                    log.info("Send sms message. message id [{}]. message content id [{}]. done.", message.getId(), message.getContentId());
                } else {
                    this.messageContentService.fail(message.getContentId(), "No sms provider found");
                    log.error("Send sms message. message id [{}]. message content id [{}]. failed.", message.getId(), message.getContentId());
                }
            } catch (Exception e) {
                this.messageContentService.fail(message.getContentId(), "", ExceptionUtils.getStackTraceAsString(e));
                log.error("Send sms message. message id [{}]. message content id [{}]. failed.", message.getId(), message.getContentId(), e);
            }
        }
    }

    @Autowired(required = false)
    public void setSmsSender(SmsFactory smsFactory) {
        this.smsFactory = smsFactory;
    }

    @Autowired
    public void setMessageContentService(MessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

    @Autowired
    public void setConfigApi(ConfigApi configApi) {
        this.configApi = configApi;
    }

}
