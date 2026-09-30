package cc.wdev.platform.commons.extensions.sensitive.mark;

import tools.jackson.databind.module.SimpleModule;

public class SensitiveMarkModule extends SimpleModule {

    public SensitiveMarkModule() {
        this.addSerializer(String.class, new SensitiveMarkSerializer());
        this.addDeserializer(String.class, new SensitiveMarkDeserializer());
    }

}
