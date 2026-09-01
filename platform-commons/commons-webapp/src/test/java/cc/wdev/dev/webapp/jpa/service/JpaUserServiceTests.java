package cc.wdev.dev.webapp.jpa.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.webapp.jpa.domain.entity.JpaUserEntity;
import cc.wdev.webapp.jpa.service.JpaUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
public class JpaUserServiceTests extends BaseTests {

    @Autowired
    JpaUserService jpaUserService;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.jpaUserService);
    }

    @Test
    public void countTest() {
        long count = this.jpaUserService.count("admin");
        Assertions.assertTrue(count >= 0);
    }

    @Test
    public void versionTest() {
        JpaUserEntity user = new JpaUserEntity();
        user.setUsername("admin");
        this.jpaUserService.save(user);

        Long id = user.getId();
        Assertions.assertNotNull(id);

        user = this.jpaUserService.findById(id);
        user.setUsername(LocalDateTime.now().toString());
        this.jpaUserService.save(user);
        this.jpaUserService.flush();
        Assertions.assertNotNull(user.getVersion());

        user = this.jpaUserService.findById(id);
        user.setUsername(LocalDateTime.now().toString());
        this.jpaUserService.save(user);
        this.jpaUserService.flush();
        Assertions.assertNotNull(user.getVersion());
    }

}
