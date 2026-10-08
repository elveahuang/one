package cc.wdev.platform.commons.extensions.sensitive.mark;

import cc.wdev.platform.commons.annotations.SensitiveMark;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveManager;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveService;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.BeansException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Objects;

/**
 * @author elvea
 */
public class SensitiveMarkDeserializer extends StdDeserializer<String> {

    private SensitiveMarkStrategy strategy;

    public SensitiveMarkDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (this.strategy != null) {
            try {
                SensitiveService sensitiveService = SensitiveManager.getService();
                if (ObjectUtil.isNotNull(sensitiveService)) {
                    return parser.getValueAsString();
                } else {
                    return parser.getValueAsString();
                }
            } catch (BeansException e) {
                return parser.getValueAsString();
            }
        } else {
            return parser.getValueAsString();
        }
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        if (Objects.isNull(property)) {
            return new SensitiveMarkDeserializer();
        }
        SensitiveMark annotation = property.getAnnotation(SensitiveMark.class);
        if (annotation != null) {
            this.strategy = annotation.strategy();
            return this;
        }
        return new SensitiveMarkDeserializer();
    }

}
