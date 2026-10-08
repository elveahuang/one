package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class AiAgentApiTests extends BaseTests {

    @Autowired
    private AiAgentApi aiAgentApi;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.aiAgentApi);
        this.aiAgentApi.initialize();
    }

}
