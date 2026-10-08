package cc.wdev.platform.system.commons.controller.exchange;

import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.system.commons.api.CaptchaApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
public class CaptchaExchangeController {

    private final CaptchaApi captchaApi;

    /**
     * @see CaptchaApi#generate(CaptchaRequest)
     */
    @PostMapping(EXCHANGE_PREFIX + "/captcha/generate")
    public Captcha generate(@RequestBody CaptchaRequest request) throws Exception {
        return this.captchaApi.generate(request);
    }

    /**
     * @see CaptchaApi#check(CaptchaCheckRequest)
     */
    @PostMapping(EXCHANGE_PREFIX + "/captcha/check")
    public boolean check(@RequestBody CaptchaCheckRequest request) {
        return this.captchaApi.check(request);
    }

}
