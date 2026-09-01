package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author elvea
 */
@Slf4j
public abstract class DateUtils {

    /**
     * 获取当前日期
     */
    public static Date date() {
        return new Date();
    }

    /**
     * 格式化日期
     */
    public static String format(Date date, String pattern) {
        return format(date, pattern, DateTimeConstants.TIME_ZONE_DEFAULT);
    }

    /**
     * 格式化日期
     */
    public static String format(Date date, DateTimeConstants.Pattern pattern) {
        return format(date, getPattern(pattern), DateTimeConstants.TIME_ZONE_DEFAULT);
    }

    /**
     * 格式化日期
     */
    public static String format(Date date, DateTimeConstants.Pattern pattern, TimeZone timeZone) {
        return format(date, getPattern(pattern), timeZone);
    }

    /**
     * 格式化日期
     */
    public static String format(Date date, String pattern, TimeZone timeZone) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setTimeZone(timeZone);
            return format.format(date);
        }
        return null;
    }

    /**
     * 解析日期
     */
    public static Date parse(String text, String pattern) {
        return parse(text, pattern, DateTimeConstants.TIME_ZONE_DEFAULT);
    }

    /**
     * 解析日期
     */
    public static Date parse(String text, DateTimeConstants.Pattern pattern) {
        return parse(text, getPattern(pattern), DateTimeConstants.TIME_ZONE_DEFAULT);
    }

    /**
     * 解析日期
     */
    public static Date parse(String text, DateTimeConstants.Pattern pattern, TimeZone timeZone) {
        return parse(text, getPattern(pattern), timeZone);
    }

    /**
     * 解析日期
     */
    public static Date parseDate(String text) {
        return parse(text, DateTimeConstants.Pattern.DATE, DateTimeConstants.TIME_ZONE_DEFAULT);
    }

    /**
     * 解析日期
     */
    public static Date parse(String text, String pattern, TimeZone timeZone) {
        if (StringUtils.isNotEmpty(text)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setTimeZone(timeZone);
                return format.parse(text);
            } catch (ParseException e) {
                log.error("DateUtils parse failed. text [{}] - pattern [{}].", text, pattern, e);
            }
        }
        return null;
    }

    /**
     * 获取预设的解析器
     */
    public static String getPattern(DateTimeConstants.Pattern pattern) {
        return switch (pattern) {
            case TIME -> DateTimeConstants.DEFAULT_TIME_PATTERN;
            case SIMPLE_TIME -> DateTimeConstants.DEFAULT_SIMPLE_TIME_PATTERN;
            case FULL_TIME -> DateTimeConstants.DEFAULT_FULL_TIME_PATTERN;
            case DATE_TIME -> DateTimeConstants.DEFAULT_DATE_TIME_PATTERN;
            case SIMPLE_DATE_TIME -> DateTimeConstants.DEFAULT_SIMPLE_DATE_TIME_PATTERN;
            case FULL_DATE_TIME -> DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN;
            case DATE -> DateTimeConstants.DEFAULT_DATE_PATTERN;
            default -> throw new IllegalStateException("Unsupported pattern: " + pattern);
        };
    }

}
