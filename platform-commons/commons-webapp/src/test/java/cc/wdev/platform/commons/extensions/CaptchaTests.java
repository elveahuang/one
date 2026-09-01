package cc.wdev.platform.commons.extensions;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.commons.extensions.captcha.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class CaptchaTests extends BaseTests {

    @Autowired
    private CaptchaService captchaService;

    @Test
    public void base() throws Exception {
        Assertions.assertNotNull(this.captchaService);
    }

    @Test
    public void codeCaptchaTest() throws Exception {
        Assertions.assertNotNull(this.captchaService);

        CaptchaRequest request = CaptchaRequest.builder().type(CaptchaTypeEnum.CODE).build();
        Captcha captcha = this.captchaService.generate(request);

        CaptchaCheckRequest checkRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.CODE)
            .key(captcha.getKey())
            .value(captcha.getValue())
            .build();
        boolean result = this.captchaService.check(checkRequest);

        Assertions.assertTrue(result);
    }

    @Test
    public void mailCaptchaTest() throws Exception {
        Assertions.assertNotNull(this.captchaService);

        CaptchaRequest request = CaptchaRequest.builder().type(CaptchaTypeEnum.EMAIL).email("huang@elvea.cn").build();
        Captcha captcha = this.captchaService.generate(request);

        CaptchaCheckRequest checkRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(captcha.getEmail())
            .key(captcha.getKey())
            .value(captcha.getValue())
            .build();
        boolean result = this.captchaService.check(checkRequest);

        Assertions.assertTrue(result);
    }

    @Test
    public void smsCaptchaTest() throws Exception {
        Assertions.assertNotNull(this.captchaService);

        CaptchaRequest request = CaptchaRequest.builder().type(CaptchaTypeEnum.SMS).mobileNumber("13800138000").build();
        Captcha captcha = this.captchaService.generate(request);

        CaptchaCheckRequest checkRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(captcha.getEmail())
            .key(captcha.getKey())
            .value(captcha.getValue())
            .build();
        boolean result = this.captchaService.check(checkRequest);

        Assertions.assertTrue(result);
    }

}
