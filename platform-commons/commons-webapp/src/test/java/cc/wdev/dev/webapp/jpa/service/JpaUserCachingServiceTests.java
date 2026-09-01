package cc.wdev.dev.webapp.jpa.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.webapp.jpa.service.JpaUserCachingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class JpaUserCachingServiceTests extends BaseTests {

    @Autowired
    JpaUserCachingService jpaUserCachingService;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.jpaUserCachingService);
    }

}
