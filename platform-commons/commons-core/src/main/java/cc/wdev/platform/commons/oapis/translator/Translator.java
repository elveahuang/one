package cc.wdev.platform.commons.oapis.translator;

import cc.wdev.platform.commons.enums.LangTypeEnum;

/**
 * 翻译器
 *
 * @author elvea
 */
public interface Translator {

    /**
     * 翻译
     *
     * @param sourceLang 源语言
     * @param targetLang 目标语言
     * @param text       文本
     * @return 翻译文本
     */
    String translate(String sourceLang, String targetLang, String text);

    /**
     * 翻译
     *
     * @param sourceLang 源语言
     * @param targetLang 目标语言
     * @param text       文本
     * @return 翻译文本
     */
    default String translate(LangTypeEnum sourceLang, LangTypeEnum targetLang, String text) {
        return this.translate(sourceLang.getValue(), targetLang.getValue(), text);
    }

}
