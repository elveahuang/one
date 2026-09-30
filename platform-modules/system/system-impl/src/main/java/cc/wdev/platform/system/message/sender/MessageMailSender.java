package cc.wdev.platform.system.message.sender;

import cc.wdev.platform.commons.core.mail.*;
import cc.wdev.platform.commons.utils.ExceptionUtils;
import cc.wdev.platform.system.config.api.ConfigApi;
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
public class MessageMailSender implements MessageSender {

    private MailFactory mailFactory;

    private ConfigApi configApi;

    private MessageContentService messageContentService;

    @Override
    public void send(SendMessageDto message) {
        // 检查邮件服务是否已经启动
        if (this.mailFactory == null) {
            log.info("Send mail message. message id [{}]. message content id [{}]. failed. mail is disabled.", message.getId(), message.getContentId());
            return;
        }

        // 获取邮箱服务器配置
        MailConfig config = this.configApi.getMailConfig();

        for (MessageRecipientDto recipient : message.getRecipients()) {
            try {
                log.info("Send mail message. message id [{}]. message content id [{}]. start", message.getId(), message.getContentId());

                if (!config.isEnabled()) {
                    log.info("Send mail message. message id [{}]. message content id [{}]. failed. mail server is disabled.", message.getId(), message.getContentId());
                    this.messageContentService.fail(message.getContentId(), "Mail server is disabled", "");
                    continue;
                }

                MailSender sender = this.mailFactory.getMailSender(config);
                MailResult result = sender.send(MailBody.builder()
                    .subject(message.getSubject())
                    .content(message.getContent())
                    .to(recipient.getEmail())
                    .build()
                );
                this.messageContentService.success(message.getContentId(), result.getResponse());
                log.info("Send mail message. message id [{}]. message content id [{}]. done", message.getId(), message.getContentId());
            } catch (Exception e) {
                this.messageContentService.fail(message.getContentId(), "", ExceptionUtils.getStackTraceAsString(e));
                log.error("Send mail message. message id [{}]. message content id [{}]. failed.", message.getId(), message.getContentId(), e);
            }
        }
    }

    @Autowired(required = false)
    public void setMailFactory(MailFactory mailFactory) {
        this.mailFactory = mailFactory;
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
