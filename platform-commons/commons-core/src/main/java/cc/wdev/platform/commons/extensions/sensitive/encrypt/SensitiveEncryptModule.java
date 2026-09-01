package cc.wdev.platform.commons.extensions.sensitive.encrypt;

import tools.jackson.databind.module.SimpleModule;

/**
 * @author elvea
 */
public class SensitiveEncryptModule extends SimpleModule {

    public SensitiveEncryptModule() {
        this.addSerializer(String.class, new SensitiveEncryptSerializer());
        this.addDeserializer(String.class, new SensitiveEncryptDeserializer());
    }

}
