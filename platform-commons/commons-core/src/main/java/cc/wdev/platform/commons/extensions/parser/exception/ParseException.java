package cc.wdev.platform.commons.extensions.parser.exception;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import lombok.Getter;

import java.io.Serial;

/**
 * 文件解析异常
 *
 * @author elvea
 */
@Getter
public class ParseException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ParseException(ResponseCodeEnum responseCode) {
        super(responseCode);
    }

    public ParseException(ResponseCodeEnum responseCode, String message) {
        super(message, null);
        this.setResponseCode(responseCode);
    }

    public ParseException(ResponseCodeEnum responseCode, String message, Throwable cause) {
        super(message, cause);
        this.setResponseCode(responseCode);
    }

}
