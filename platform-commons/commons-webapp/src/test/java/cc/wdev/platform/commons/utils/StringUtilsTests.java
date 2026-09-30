package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
@Slf4j
public class StringUtilsTests {

    @Test
    public void baseTests() {
        Assertions.assertTrue(StringUtils.isBlank(""));
        Assertions.assertTrue(StringUtils.isNotBlank("A"));
        Assertions.assertTrue(StringUtils.isEmpty(""));
        Assertions.assertTrue(StringUtils.isNotEmpty("A"));
        Assertions.assertEquals("A", StringUtils.toUpperCase("a"));
        Assertions.assertEquals("a", StringUtils.toLowerCase("A"));
    }

    /**
     * 生成随机字符串
     */
    @Test
    public void randomTests() {
        log.info("randomString - {}", StringUtils.randomString(12));
        log.info("randomString - {}", StringUtils.randomString(12));
        log.info("randomString - {}", StringUtils.randomString(12));
        log.info("randomAlphabetic - {}", StringUtils.randomAlphabetic(12));
        log.info("randomAlphabetic - {}", StringUtils.randomAlphabetic(12));
        log.info("randomAlphabetic - {}", StringUtils.randomAlphabetic(12));
        log.info("randomNumeric - {}", StringUtils.randomNumeric(12));
        log.info("randomNumeric - {}", StringUtils.randomNumeric(12));
        log.info("randomNumeric - {}", StringUtils.randomNumeric(12));
    }

    @Test
    public void uuidTests() {
        String uuid = StringUtils.uuid();
        log.info("uuid: {}", uuid);
        Assertions.assertNotNull(uuid);
    }

}
