package cc.wdev.platform.commons.extensions.sensitive.mark;

import cc.wdev.platform.commons.annotations.SensitiveMark;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveManager;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveService;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Objects;

/**
 * @author elvea
 */
@Slf4j
public class SensitiveMarkSerializer extends StdSerializer<String> {

    private SensitiveMarkStrategy strategy;

    protected SensitiveMarkSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator generator, SerializationContext context) throws JacksonException {
        if (this.strategy != null) {
            try {
                SensitiveService sensitiveService = SensitiveManager.getService();
                if (ObjectUtil.isNotNull(sensitiveService)) {
                    generator.writeString(strategy.handler().apply(value));
                } else {
                    generator.writeString(value);
                }
            } catch (BeansException e) {
                generator.writeString(value);
            }
        } else {
            generator.writeString(value);
        }
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext context, BeanProperty property) {
        if (Objects.isNull(property)) {
            return this;
        }
        SensitiveMark annotation = property.getAnnotation(SensitiveMark.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.strategy = annotation.strategy();
            return this;
        }
        return context.findValueSerializer(property.getType());
    }

}
