package cc.wdev.platform.commons.oapis.sms;

/**
 * @author elvea
 */
public interface SmsSender<C, R> {

    R send(SmsBody body) throws Exception;

    R send(C config, SmsBody body) throws Exception;

}
