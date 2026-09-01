package cc.wdev.platform.commons.utils.jackson;

import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.utils.DateUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.time.TimeZoneManager;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Date;

/**
 * @author elvea
 */
public class DateSerializer extends StdSerializer<Date> {

    private String pattern;

    private boolean timeZoneConvert = false;

    public DateSerializer() {
        super(Date.class);
    }

    public DateSerializer(String pattern, boolean timeZoneConvert) {
        super(Date.class);
        this.pattern = pattern;
        this.timeZoneConvert = timeZoneConvert;
    }

    @Override
    public void serialize(Date date, JsonGenerator generator, SerializationContext provider) {
        String pattern = StringUtils.isNotEmpty(this.pattern) ? this.pattern : DateTimeConstants.DEFAULT_DATE_TIME_PATTERN;
        if (timeZoneConvert) {
            generator.writeString(DateUtils.format(date, pattern, TimeZoneManager.getResolver().resolveUserTimeZone()));
        } else {
            generator.writeString(DateUtils.format(date, pattern));
        }
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext provider, BeanProperty property) {
        if (!ObjectUtils.isEmpty(property)) {
            com.fasterxml.jackson.annotation.JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
            JsonFormat annotation = property.getAnnotation(JsonFormat.class);
            if (annotation != null) {
                return new DateSerializer(annotation.pattern(), annotation.timeZoneConvert());
            }
        }
        return new DateSerializer();
    }

}
