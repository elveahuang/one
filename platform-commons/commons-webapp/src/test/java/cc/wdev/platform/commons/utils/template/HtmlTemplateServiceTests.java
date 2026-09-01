package cc.wdev.platform.commons.utils.template;

import cc.wdev.dev.webapp.BaseTests;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class HtmlTemplateServiceTests extends BaseTests {

    @Autowired
    private HtmlTemplateService htmlTemplateService;

    @Test
    public void base() throws Exception {
        org.springframework.core.io.ClassPathResource resource = new ClassPathResource("html/tpl.html");
        String template = resource.getContentAsString(StandardCharsets.UTF_8);

        Map<String, Object> params = Maps.newHashMap();
        params.put("title", "Title");
        params.put("content", "Content Content");
        String html = htmlTemplateService.toHtml(template, params);
        Assertions.assertNotNull(html);

    }

}
