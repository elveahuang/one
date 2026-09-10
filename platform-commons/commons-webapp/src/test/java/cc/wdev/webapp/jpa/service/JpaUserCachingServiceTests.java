package cc.wdev.webapp.jpa.service;

import cc.wdev.webapp.BaseTests;
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
