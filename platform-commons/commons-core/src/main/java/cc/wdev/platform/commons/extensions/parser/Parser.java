package cc.wdev.platform.commons.extensions.parser;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.extensions.parser.domain.ParseResult;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;

/**
 * 文件解析器
 *
 * @author elvea
 */
public interface Parser {

    /**
     * 解析器负责的文件分类
     */
    MediaTypeCategoryEnum category();

    /**
     * 是否支持解析该请求
     */
    boolean supports(ParseRequest request);

    /**
     * 执行解析
     */
    ParseResult parse(ParseRequest request) throws ParseException;

    /**
     * 同一分类下多个解析器时的执行优先级，越小越优先
     */
    default int getOrder() {
        return 0;
    }

}
