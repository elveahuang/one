package cc.wdev.platform.commons.ai.sdk;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.webapp.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class OpenAiSdkTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiManager);
    }

}
