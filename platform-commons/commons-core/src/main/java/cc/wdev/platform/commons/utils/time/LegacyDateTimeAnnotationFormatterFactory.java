package cc.wdev.platform.commons.utils.time;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * @author elvea
 */
public class LegacyDateTimeAnnotationFormatterFactory extends EmbeddedValueResolutionSupport
    implements AnnotationFormatterFactory<DateTimeFormat> {

    private static final Set<Class<?>> FIELD_TYPES;

    static {
        FIELD_TYPES = Set.of(Date.class, Calendar.class);
    }

    private final TimeZoneResolver resolver;

    public LegacyDateTimeAnnotationFormatterFactory(TimeZoneResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    @NonNull
    public Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    @Override
    @NonNull
    public Printer<?> getPrinter(@NonNull DateTimeFormat annotation, @NonNull Class<?> fieldType) {
        return getFormatter(annotation, fieldType);
    }

    @Override
    @NonNull
    public Parser<?> getParser(@NonNull DateTimeFormat annotation, @NonNull Class<?> fieldType) {
        return getFormatter(annotation, fieldType);
    }

    protected Formatter<Date> getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        LegacyDateTimeFormatter formatter = new LegacyDateTimeFormatter();
        formatter.setResolver(resolver);
        formatter.setSource(annotation);
        formatter.setPattern(annotation.pattern());
        formatter.setTimeZoneConvert(annotation.timeZoneConvert());
        return formatter;
    }

}
