package cc.wdev.platform.commons.core.cache.service;

import cc.wdev.dev.webapp.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class CacheServiceTests extends BaseTests {

    @Autowired
    CacheService cacheService;

    public void base() throws Exception {
        Assertions.assertNotNull(this.cacheService);
    }

}
