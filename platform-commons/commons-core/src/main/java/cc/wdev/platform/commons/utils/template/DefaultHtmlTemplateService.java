package cc.wdev.platform.commons.utils.template;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

/**
 * @author elvea
 */
public class DefaultHtmlTemplateService implements HtmlTemplateService {

    private final TemplateEngine templateEngine;

    public DefaultHtmlTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String toHtml(String template, Map<String, Object> params) {
        Context context = new Context(Locale.getDefault(), params);
        return templateEngine.process(template, context);
    }

}
