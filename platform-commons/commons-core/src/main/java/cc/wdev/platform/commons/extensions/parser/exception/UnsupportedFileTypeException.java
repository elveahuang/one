package cc.wdev.platform.commons.extensions.parser.exception;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;

import java.io.Serial;

/**
 * 不支持的文件类型
 *
 * @author elvea
 */
public class UnsupportedFileTypeException extends ParseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnsupportedFileTypeException(String message) {
        super(ResponseCodeEnum.PARSE_UNSUPPORTED_TYPE, message);
    }

}
