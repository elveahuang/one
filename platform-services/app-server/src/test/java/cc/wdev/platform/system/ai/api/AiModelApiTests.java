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
public class AiModelApiTests extends BaseTests {

    @Autowired
    private AiModelApi aiModelApi;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.aiModelApi);
        this.aiModelApi.initialize();
    }

}
