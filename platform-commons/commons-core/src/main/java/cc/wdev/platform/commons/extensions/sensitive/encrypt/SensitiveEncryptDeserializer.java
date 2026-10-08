package cc.wdev.platform.commons.extensions.sensitive.encrypt;

import cc.wdev.platform.commons.annotations.SensitiveEncrypt;
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

public class SensitiveEncryptDeserializer extends StdDeserializer<String> {

    private SensitiveEncryptStrategy strategy;

    public SensitiveEncryptDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (this.strategy != null) {
            try {
                SensitiveService sensitiveService = SensitiveManager.getService();
                if (ObjectUtil.isNotNull(sensitiveService)) {
                    return this.strategy.decryptHandler().apply(parser.getValueAsString());
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
        SensitiveEncrypt annotation = property.getAnnotation(SensitiveEncrypt.class);
        if (annotation != null) {
            this.strategy = annotation.strategy();
            return this;
        }
        return new SensitiveEncryptDeserializer();
    }

}
