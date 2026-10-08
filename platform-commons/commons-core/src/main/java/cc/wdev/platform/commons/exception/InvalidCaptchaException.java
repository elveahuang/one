package cc.wdev.platform.commons.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常
 *
 * @author elvea
 */
public class InvalidCaptchaException extends AuthenticationException {

    public InvalidCaptchaException(String msg) {
        super(msg);
    }

    public InvalidCaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
