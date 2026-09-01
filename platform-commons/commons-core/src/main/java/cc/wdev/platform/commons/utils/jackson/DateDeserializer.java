package cc.wdev.platform.commons.utils.jackson;

import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.utils.DateUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.time.TimeZoneManager;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Date;

/**
 * @author elvea JsonDeserializer
 */
public class DateDeserializer extends StdDeserializer<Date> {

    private String pattern;

    private boolean timeZoneConvert = false;

    public DateDeserializer() {
        super(Date.class);
    }

    public DateDeserializer(String pattern, boolean timeZoneConvert) {
        super(Date.class);
        this.pattern = pattern;
        this.timeZoneConvert = timeZoneConvert;
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) {
        String pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN;
        if (StringUtils.isNotEmpty(this.pattern)) {
            pattern = this.pattern;
        }
        if (timeZoneConvert) {
            return DateUtils.parse(parser.getValueAsString(), pattern, TimeZoneManager.getResolver().resolveUserTimeZone());
        } else {
            return DateUtils.parse(parser.getValueAsString(), pattern);
        }
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        com.fasterxml.jackson.annotation.JsonFormat.Value format = findFormatOverrides(context, property, handledType());
        JsonFormat annotation = property.getAnnotation(JsonFormat.class);
        if (annotation != null) {
            return new DateDeserializer(annotation.pattern(), annotation.timeZoneConvert());
        }
        return new DateDeserializer();
    }

}
