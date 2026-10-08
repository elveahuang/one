package cc.wdev.platform.commons.utils.i18n;

/**
 * 默认语言解析器，直接返回当前系统的默认时区。
 *
 * @author elvea
 */
public record DefaultLanguageResolver(String language) implements LanguageResolver {

    @Override
    public String resolveLanguage() {
        return this.language;
    }

}
