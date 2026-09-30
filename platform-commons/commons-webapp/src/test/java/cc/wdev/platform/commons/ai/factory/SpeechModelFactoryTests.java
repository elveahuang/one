package cc.wdev.platform.commons.ai.factory;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.webapp.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class SpeechModelFactoryTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiManager);
    }

}
