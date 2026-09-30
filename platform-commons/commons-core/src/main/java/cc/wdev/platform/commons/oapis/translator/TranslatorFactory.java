package cc.wdev.platform.commons.oapis.translator;

import cc.wdev.platform.commons.oapis.translator.aliyun.AliyunTranslator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public class TranslatorFactory {

    private final TranslatorConfig config;

    public TranslatorFactory(TranslatorConfig config) {
        this.config = config;
    }

    public Translator getTranslator() {
        return getAliyunTranslator();
    }

    public AliyunTranslator getAliyunTranslator() {
        return this.getAliyunTranslator(this.config.getAliyun());
    }

    public AliyunTranslator getAliyunTranslator(AliyunTranslator.Config config) {
        return new AliyunTranslator(config);
    }

}
