package cc.wdev.platform.commons.core.mail;

import cc.wdev.platform.commons.core.mail.spring.SpringMailSender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public record MailFactory(MailConfig config) {

    public MailSender getMailSender() {
        return getMailSender(this.config);
    }

    public MailSender getMailSender(MailConfig config) {
        return new SpringMailSender(config);
    }

}
