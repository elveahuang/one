package cc.wdev.platform.commons.utils.jackson;

import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.utils.DateTimeUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.time.TimeZoneManager;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author elvea
 */
@Slf4j
public final class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private String pattern;

    private boolean timeZoneConvert = false;

    public LocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    public LocalDateTimeDeserializer(String pattern, boolean timeZoneConvert) {
        super(LocalDateTime.class);

        this.pattern = pattern;
        this.timeZoneConvert = timeZoneConvert;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) {
        log.info("deserialize [{}] pattern [{}].", parser.getString(), this.pattern);
        LocalDateTime localDateTime = DateTimeUtils.parse(parser.getString(), getFormatter(), LocalDateTime.class);
        if (timeZoneConvert) {
            return DateTimeUtils.transfer(localDateTime, TimeZoneManager.getResolver().resolveUserZoneId(), TimeZoneManager.getResolver().resolveSystemZoneId());
        } else {
            return localDateTime;
        }
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        log.info("createContextual [{}].", property.getName());
        JsonFormat annotation = property.getAnnotation(JsonFormat.class);
        if (annotation != null) {
            return new LocalDateTimeDeserializer(annotation.pattern(), annotation.timeZoneConvert());
        }
        return new LocalDateTimeDeserializer();
    }

    private DateTimeFormatter getFormatter() {
        DateTimeFormatter formatter = null;
        if (StringUtils.isNotEmpty(this.pattern)) {
            formatter = DateTimeFormatter.ofPattern(this.pattern);
        }
        return formatter;
    }

}
