package cc.wdev.platform.system.config.api;

import cc.wdev.platform.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class BizTypeApiTests extends BaseTests {

    @Autowired
    private BizTypeApi bizTypeApi;

    @Test
    public void initializeTest() {
        bizTypeApi.initialize();
    }

}
