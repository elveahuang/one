package cc.wdev.dev.webapp.mybatis.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.webapp.mybatis.service.MpUserCachingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class MpUserCachingServiceTests extends BaseTests {

    @Autowired
    MpUserCachingService mpUserCachingService;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.mpUserCachingService);
    }

}
