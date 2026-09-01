package cc.wdev.platform.commons.utils.time;

import cc.wdev.platform.commons.utils.DateTimeUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * StandardDateTimeFormatter
 *
 * @author elvea
 */
@Setter
@Slf4j
public class StandardDateTimeFormatter implements Formatter<TemporalAccessor> {

    private final TimeZoneResolver resolver;

    private final Class<? extends TemporalAccessor> temporalAccessorType;

    private String pattern;

    private boolean timeZoneConvert = false;

    public StandardDateTimeFormatter(TimeZoneResolver resolver, Class<? extends TemporalAccessor> temporalAccessorType) {
        this.resolver = resolver;
        this.temporalAccessorType = temporalAccessorType;
    }

    @Override
    @NonNull
    public TemporalAccessor parse(@NonNull String text, @NonNull Locale locale) throws ParseException {
        log.info("StandardDateTimeFormatter parse - text {}", text);
        if (LocalDateTime.class == this.temporalAccessorType) {
            LocalDateTime localDateTime = DateTimeUtils.parse(text, getFormatter(), LocalDateTime.class);
            if (timeZoneConvert) {
                return DateTimeUtils.transfer(localDateTime, resolver.resolveUserZoneId(), resolver.resolveSystemZoneId());
            } else {
                return localDateTime;
            }
        }
        return DateTimeUtils.parse(text, getFormatter(), temporalAccessorType);
    }

    @Override
    @NonNull
    public String print(@NonNull TemporalAccessor date, @NonNull Locale locale) {
        log.info("StandardDateTimeFormatter print - date {}.", date);
        if (LocalDateTime.class == this.temporalAccessorType && this.timeZoneConvert) {
            LocalDateTime targetLocalDateTime = DateTimeUtils.transfer((LocalDateTime) date, resolver.resolveSystemZoneId(), resolver.resolveUserZoneId());
            return DateTimeUtils.format(targetLocalDateTime, getFormatter());
        }
        return DateTimeUtils.format(date, getFormatter());
    }

    protected DateTimeFormatter getFormatter() {
        DateTimeFormatter formatter = null;
        if (StringUtils.isNotEmpty(this.pattern)) {
            formatter = DateTimeFormatter.ofPattern(this.pattern);
        }
        return formatter;
    }

}
