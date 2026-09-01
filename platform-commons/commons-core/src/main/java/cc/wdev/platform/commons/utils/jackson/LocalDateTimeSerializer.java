package cc.wdev.platform.commons.utils.jackson;

import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.utils.DateTimeUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.time.TimeZoneManager;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author elvea
 */
public final class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    private String pattern;

    private boolean timeZoneConvert = false;

    public LocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    public LocalDateTimeSerializer(String pattern, boolean timeZoneConvert) {
        super(LocalDateTime.class);
        this.pattern = pattern;
        this.timeZoneConvert = timeZoneConvert;
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator generator, SerializationContext provider) {
        if (timeZoneConvert) {
            LocalDateTime targetLocalDateTime = DateTimeUtils.transfer(localDateTime, TimeZoneManager.getResolver().resolveSystemZoneId(), TimeZoneManager.getResolver().resolveUserZoneId());
            generator.writeString(DateTimeUtils.format(targetLocalDateTime, getFormatter()));
        } else {
            generator.writeString(DateTimeUtils.format(localDateTime, DateTimeConstants.Pattern.DATE_TIME));
        }
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext provider, BeanProperty property) {
        if (!ObjectUtils.isEmpty(property)) {
            com.fasterxml.jackson.annotation.JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
            JsonFormat annotation = property.getAnnotation(JsonFormat.class);
            if (annotation != null) {
                return new LocalDateTimeSerializer(annotation.pattern(), annotation.timeZoneConvert());
            }
        }
        return new LocalDateTimeSerializer();
    }

    private DateTimeFormatter getFormatter() {
        String pattern = StringUtils.isNotEmpty(this.pattern) ? this.pattern : DateTimeConstants.DEFAULT_DATE_TIME_PATTERN;
        DateTimeFormatter formatter = null;
        if (StringUtils.isNotEmpty(this.pattern)) {
            formatter = DateTimeFormatter.ofPattern(this.pattern);
        }
        return formatter;
    }

}
