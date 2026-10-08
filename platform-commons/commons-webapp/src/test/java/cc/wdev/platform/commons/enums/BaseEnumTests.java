package cc.wdev.platform.commons.enums;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
@Slf4j
public class BaseEnumTests {

    @Test
    public void base() {
        CaptchaTypeEnum captchaTypeEnum = BaseEnum.getEnumByValue("SMS", CaptchaTypeEnum.class);
        Assertions.assertNotNull(captchaTypeEnum);
    }

}
