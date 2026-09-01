package cc.wdev.platform.commons.core.mail;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.autoconfigure.core.properties.TestProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class MailSenderTests extends BaseTests {

    @Autowired
    private TestProperties properties;

    @Autowired
    private MailFactory mailFactory;

    @Test
    public void base() throws Exception {
        MailBody body = MailBody.builder()
            .subject("Test")
            .content("Java Mail Sender")
            .to(this.properties.getEmail())
            .build();
        mailFactory.getMailSender().send(body);
    }

}
