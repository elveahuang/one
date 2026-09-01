package cc.wdev.platform.commons.utils;

import cc.wdev.dev.webapp.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
@Slf4j
public class SecurityUtilsTests extends BaseTests {

    @Test
    public void baseTest() {
        String noop = "{noop}" + StringUtils.randomString(32);
        log.info("Noop : {}", noop);
        Assertions.assertNotNull(noop);

        String code = StringUtils.randomString(16);
        String text = StringUtils.randomString(32);
        String hash = "{SHA-256}" + EncryptUtils.sha256(text);
        log.info("code : {}", code);
        log.info("text : {}", text);
        log.info("hash : {}", hash);
        Assertions.assertNotNull(hash);
        boolean matches = SecurityUtils.getPasswordEncoder().matches(text, hash);
        Assertions.assertTrue(matches);
    }

    @Test
    public void bcryptTest() {
        String plainPassword, encryptedPassword;

        plainPassword = "admin";
        encryptedPassword = SecurityUtils.encode(plainPassword);
        log.info("plainPassword : {} | encryptedPassword : {}", plainPassword, encryptedPassword);
        Assertions.assertTrue(SecurityUtils.matches(plainPassword, encryptedPassword));

        plainPassword = "test";
        encryptedPassword = SecurityUtils.encode(plainPassword);
        log.info("plainPassword : {} | encryptedPassword : {}", plainPassword, encryptedPassword);
        Assertions.assertTrue(SecurityUtils.matches(plainPassword, encryptedPassword));
    }

}
