package cc.wdev.platform.commons.ai.service;

import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.webapp.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class SpeechServiceTests extends BaseTests {

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

}
