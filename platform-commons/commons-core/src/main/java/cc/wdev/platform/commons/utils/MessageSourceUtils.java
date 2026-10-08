package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import java.util.Locale;

/**
 * @author elvea
 */
@Slf4j
public class MessageSourceUtils implements MessageSourceAware {

    private static MessageSourceAccessor messageSourceAccessor;

    /**
     * @see MessageSourceAware#setMessageSource(MessageSource)
     */
    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource must not be null.");
        MessageSourceUtils.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

    /**
     * 获取多语言文本
     */
    public static String getMessage(String code, Locale locale) {
        return messageSourceAccessor.getMessage(code, locale);
    }

    /**
     * 获取多语言文本
     */
    public static String getMessage(String code, String defaultMessage, Locale locale) {
        return messageSourceAccessor.getMessage(code, defaultMessage, locale);
    }

    /**
     * 获取多语言文本
     */
    public static String getMessage(String code, Object[] args, Locale locale) {
        return messageSourceAccessor.getMessage(code, args, locale);
    }

    /**
     * 获取多语言文本
     */
    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return messageSourceAccessor.getMessage(code, args, defaultMessage, locale);
    }

}
