package cc.wdev.platform.commons.web.servlet.handler;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.exception.SystemException;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.WebServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author elvea
 */
@Slf4j
public abstract class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> R<T> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("handleNoHandlerFoundException exception : {}", e.getMessage(), e);
        return R.fail(ResponseCodeEnum.NOT_FOUNT);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> R<T> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException exception : {}", e.getMessage(), e);
        return R.fail(ResponseCodeEnum.NOT_FOUNT);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Map<String, String>> handleHttpRequestMethodNotSupportedException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException exception", e);

        Map<String, String> errors = new HashMap<>();
        StringBuilder messageBuilder = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            messageBuilder.append(", ").append(errorMessage);
        });

        try {
            log.error("handleMethodArgumentNotValidException exception. details : [{}]", JacksonUtils.toJson(errors));
        } catch (Exception ex) {
            log.error("Fail to log error details", ex);
        }

        R<Map<String, String>> resp = R.fail(ResponseCodeEnum.PARAM_ERROR);
        resp.setMessage(resp.getMessage() + messageBuilder);
        resp.setData(errors);
        return resp;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> R<T> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        try {
            log.error("handleMissingServletRequestParameterException url : [{}]. parameter : [{}]. header : [{}]",
                WebServletUtils.getRequestURI(),
                WebServletUtils.getParameterAsJson(),
                WebServletUtils.getHeaderAsJson(), e);
        } catch (Exception ex) {
            log.error("MissingServletRequestParameterException - {}", e.getMessage(), ex);
        }
        return R.fail(ResponseCodeEnum.PARAM_NOT_PRESENT);
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleInvalidBearerTokenException(InvalidBearerTokenException e) {
        log.error("handleInvalidBearerTokenException exception : {}", e.getMessage(), e);
        return R.fail(ResponseCodeEnum.PARAM_NOT_PRESENT);
    }

    /**
     * 服务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleServiceException(ServiceException e) {
        log.error("handleSystemException exception : {}", e.getMessage(), e);
        return R.fail(e.getResponseCode());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleSystemException(SystemException e) {
        log.error("handleSystemException exception : {}", e.getMessage(), e);
        return R.error(e.getLocalizedMessage());
    }

    /**
     * 数据库异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        log.error("handleException exception : {}", e.getMessage(), e);
        if (message.contains("value too long")) {
            return R.fail(ResponseCodeEnum.PARAM_ERROR);
        }
        return R.error(e.getLocalizedMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleException(Exception e) {
        log.error("handleException exception : {}", e.getMessage(), e);
        return R.error(e.getLocalizedMessage());
    }

}
