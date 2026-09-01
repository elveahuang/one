package cc.wdev.platform.commons.core.mail;

/**
 * @author elvea
 */
public interface MailSender {

    MailResult send(MailBody body) throws Exception;

}
