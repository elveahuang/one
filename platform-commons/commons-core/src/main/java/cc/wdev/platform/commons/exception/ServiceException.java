package cc.wdev.platform.commons.exception;

import cc.wdev.platform.commons.enums.BaseResponseCodeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Map;

/**
 * 服务异常
 *
 * @author elvea
 */
@Getter
@Setter
public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BaseResponseCodeEnum responseCode = ResponseCodeEnum.ERROR;

    public String code;

    public String message;

    public Map<String, Object> data;

    public Integer statusCode;

    public String description;

    public ServiceException() {
    }

    public ServiceException(BaseResponseCodeEnum responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.setMessage(message);
    }

    @Override
    public String getMessage() {
        if (StringUtils.isEmpty(message)) {
            return super.getMessage();
        }
        return message;
    }

}
