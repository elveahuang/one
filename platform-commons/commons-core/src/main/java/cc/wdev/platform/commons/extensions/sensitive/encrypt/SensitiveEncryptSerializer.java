package cc.wdev.platform.commons.extensions.sensitive.encrypt;

import cc.wdev.platform.commons.annotations.SensitiveEncrypt;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveManager;
import cc.wdev.platform.commons.extensions.sensitive.SensitiveService;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.BeansException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

public class SensitiveEncryptSerializer extends StdSerializer<String> {

    private SensitiveEncryptStrategy strategy;

    protected SensitiveEncryptSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator generator, SerializationContext context) throws JacksonException {
        if (this.strategy != null) {
            try {
                SensitiveService sensitiveService = SensitiveManager.getService();
                if (ObjectUtil.isNotNull(sensitiveService)) {
                    generator.writeString(strategy.encryptHandler().apply(value));
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
        SensitiveEncrypt annotation = property.getAnnotation(SensitiveEncrypt.class);
        if (annotation != null) {
            this.strategy = annotation.strategy();
            return this;
        }
        return new SensitiveEncryptSerializer();
    }

}
