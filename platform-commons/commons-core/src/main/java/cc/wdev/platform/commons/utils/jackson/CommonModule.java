package cc.wdev.platform.commons.utils.jackson;

import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author elvea
 */
public class CommonModule extends SimpleModule {

    public CommonModule() {
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        this.addDeserializer(Date.class, new DateDeserializer());
        this.addSerializer(Date.class, new DateSerializer());
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
    }

}
