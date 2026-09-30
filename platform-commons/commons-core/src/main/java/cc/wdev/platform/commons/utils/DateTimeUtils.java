package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

/**
 * @author elvea
 */
@Slf4j
public abstract class DateTimeUtils {

    public static final DateTimeFormatter TIME_FORMATTER;
    public static final DateTimeFormatter SIMPLE_TIME_FORMATTER;
    public static final DateTimeFormatter FULL_TIME_FORMATTER;
    public static final DateTimeFormatter DATE_FORMATTER;
    public static final DateTimeFormatter SIMPLE_DATE_FORMATTER;
    public static final DateTimeFormatter DATE_TIME_FORMATTER;
    public static final DateTimeFormatter SIMPLE_DATE_TIME_FORMATTER;
    public static final DateTimeFormatter FULL_DATE_TIME_FORMATTER;
    public final static LocalDateTime MIN_DATETIME;
    public final static LocalDateTime MAX_DATETIME;

    public final static int DATETIME_PAD_END_HH_MM_SS = 1;
    public final static int DATETIME_PAD_END_MM_SS = 2;
    public final static int DATETIME_PAD_END_SS = 3;

    static {

        MIN_DATETIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);

        MAX_DATETIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 0);

        TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_TIME_PATTERN).toFormatter();

        SIMPLE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_SIMPLE_TIME_PATTERN).parseDefaulting(SECOND_OF_MINUTE, 1).toFormatter();

        FULL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_FULL_TIME_PATTERN).toFormatter();

        DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_DATE_TIME_PATTERN).parseDefaulting(MILLI_OF_SECOND, 0).toFormatter();

        SIMPLE_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_SIMPLE_DATE_TIME_PATTERN).parseDefaulting(SECOND_OF_MINUTE, 0).parseDefaulting(MILLI_OF_SECOND, 0).toFormatter();

        FULL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN).toFormatter();

        DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_DATE_PATTERN).toFormatter();

        SIMPLE_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DateTimeConstants.DEFAULT_SIMPLE_DATE_PATTERN).toFormatter();

    }

    public static final Map<DateTimeConstants.Pattern, String> DATE_TIME_PATTERNS;

    static {
        Map<DateTimeConstants.Pattern, String> formats = new EnumMap<>(DateTimeConstants.Pattern.class);
        formats.put(DateTimeConstants.Pattern.DATE, DateTimeConstants.DEFAULT_DATE_PATTERN);
        formats.put(DateTimeConstants.Pattern.SIMPLE_DATE, DateTimeConstants.DEFAULT_SIMPLE_DATE_PATTERN);
        formats.put(DateTimeConstants.Pattern.DATE_TIME, DateTimeConstants.DEFAULT_DATE_TIME_PATTERN);
        formats.put(DateTimeConstants.Pattern.FULL_DATE_TIME, DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN);
        formats.put(DateTimeConstants.Pattern.SIMPLE_DATE_TIME, DateTimeConstants.DEFAULT_SIMPLE_DATE_TIME_PATTERN);
        formats.put(DateTimeConstants.Pattern.TIME, DateTimeConstants.DEFAULT_TIME_PATTERN);
        formats.put(DateTimeConstants.Pattern.SIMPLE_TIME, DateTimeConstants.DEFAULT_SIMPLE_TIME_PATTERN);
        DATE_TIME_PATTERNS = Collections.unmodifiableMap(formats);
    }

    /**
     * 转换时区
     */
    public static LocalDateTime transfer(LocalDateTime localDateTime, ZoneId targetZoneId) {
        return transfer(localDateTime, DateTimeConstants.ZONE_ID_DEFAULT, targetZoneId);
    }

    /**
     * 转换时区
     */
    public static LocalDateTime transfer(LocalDateTime localDateTime, ZoneId fromZoneId, ZoneId targetZoneId) {
        return localDateTime.atZone(fromZoneId).withZoneSameInstant(targetZoneId).toLocalDateTime();
    }

    /**
     * 格式化日期
     */
    public static <E extends TemporalAccessor> String format(E e, String patten) {
        return format(e, createFormatter(patten));
    }

    /**
     * 格式化日期
     */
    public static <E extends TemporalAccessor> String format(E e, DateTimeConstants.Pattern pattern) {
        return format(e, getFormatter(pattern));
    }

    /**
     * 格式化日期
     */
    public static <E extends TemporalAccessor> String format(E e, DateTimeFormatter formatter) {
        return formatter.format(e);
    }

    /**
     * 解析日期
     */
    public static <T extends TemporalAccessor> T parse(String text, String pattern, Class<T> classType) {
        return parse(text, createFormatter(pattern), classType);
    }

    /**
     * 解析日期
     */
    public static <T extends TemporalAccessor> T parse(String text, DateTimeConstants.Pattern pattern, Class<T> classType) {
        return parse(text, getFormatter(pattern), classType);
    }

    /**
     * 解析日期
     */
    @SuppressWarnings("unchecked")
    public static <T extends TemporalAccessor> T parse(String text, DateTimeFormatter formatter, Class<T> classType) {
        if (LocalDate.class == classType) {
            return (T) LocalDate.parse(text, formatter);
        } else if (LocalTime.class == classType) {
            return (T) LocalTime.parse(text, formatter);
        } else if (LocalDateTime.class == classType) {
            try {
                return (T) LocalDateTime.parse(text, formatter);
            } catch (Exception e) {
                return (T) LocalDateTime.parse(text);
            }
        } else if (ZonedDateTime.class == classType) {
            return (T) ZonedDateTime.parse(text, formatter);
        } else if (OffsetDateTime.class == classType) {
            return (T) OffsetDateTime.parse(text, formatter);
        } else if (OffsetTime.class == classType) {
            return (T) OffsetTime.parse(text, formatter);
        } else {
            log.error("DateTimeUtils parse failed. text [{}].", text);
            throw new IllegalStateException("Unsupported TemporalAccessor type: " + classType);
        }
    }

    /**
     * 获取预设的解析器
     */
    public static DateTimeFormatter getFormatter(DateTimeConstants.Pattern dateTimePattern) {
        return switch (dateTimePattern) {
            case DATE -> DATE_FORMATTER;
            case SIMPLE_DATE -> SIMPLE_DATE_FORMATTER;
            case DATE_TIME -> DATE_TIME_FORMATTER;
            case SIMPLE_DATE_TIME -> SIMPLE_DATE_TIME_FORMATTER;
            case FULL_DATE_TIME -> FULL_DATE_TIME_FORMATTER;
            case TIME -> TIME_FORMATTER;
            case SIMPLE_TIME -> SIMPLE_TIME_FORMATTER;
            case FULL_TIME -> FULL_TIME_FORMATTER;
            default -> throw new IllegalStateException("Unsupported pattern: " + dateTimePattern);
        };
    }

    public static DateTimeFormatter createFormatter(String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        return DateTimeFormatter.ofPattern(pattern);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date != null) {
            return LocalDateTime.ofInstant(date.toInstant(), DateTimeConstants.ZONE_ID_DEFAULT);
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    public static LocalTime toLocalTime(Date date) {
        return toLocalDateTime(date).toLocalTime();
    }

    public static LocalDateTime processStartDate(LocalDateTime localDateTime) {
        return processStartDate(localDateTime, DATETIME_PAD_END_HH_MM_SS);
    }

    public static LocalDateTime processStartDate(LocalDateTime ldt, final int padEndType) {
        if (ldt == null) {
            return LocalDateTime.MIN;
        }
        if (DATETIME_PAD_END_HH_MM_SS == padEndType) {
            ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 0, 0, 0, 0);
        } else if (DATETIME_PAD_END_MM_SS == padEndType) {
            ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), ldt.getHour(), 0, 0, 0);
        } else if (DATETIME_PAD_END_SS == padEndType) {
            ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(), 0, 0);
        }
        return ldt;
    }

    public static LocalDateTime processEndDate(LocalDateTime date) {
        return processEndDate(date, DATETIME_PAD_END_HH_MM_SS);
    }

    public static LocalDateTime processEndDate(LocalDateTime ldt, final int padEndType) {
        if (ldt == null) {
            return MAX_DATETIME;
        }
        if (DATETIME_PAD_END_HH_MM_SS == padEndType) {
            ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 23, 59, 59, 999);
        } else if (DATETIME_PAD_END_MM_SS == padEndType) {
            ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), ldt.getHour(), 59, 59, 0);
        } else if (DATETIME_PAD_END_SS == padEndType) {
            ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(), 59, 999);
        }
        return ldt;
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
