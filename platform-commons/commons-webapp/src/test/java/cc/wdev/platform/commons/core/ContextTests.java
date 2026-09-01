package cc.wdev.platform.commons.core;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.utils.MessageSourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

/**
 * @author elvea
 */
public class ContextTests extends BaseTests {

    @Autowired
    GlobalContext context;

    @Test
    public void bastTest() {
        Assertions.assertNotNull(context);
    }

    @Test
    public void messageSourceTest() {
        String text = MessageSourceUtils.getMessage("system__label__app_title", Locale.CHINA);
        Assertions.assertNotNull(text);
    }

}
