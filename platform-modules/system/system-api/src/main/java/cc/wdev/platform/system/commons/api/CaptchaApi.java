package cc.wdev.platform.system.commons.api;

import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/captcha")
public interface CaptchaApi {

    /**
     * 生成验证码
     */
    @GetExchange("/generate")
    Captcha generate(@RequestBody CaptchaRequest request) throws Exception;

    /**
     * 校验验证码
     */
    @PostExchange("/check")
    boolean check(@RequestBody CaptchaCheckRequest request);

}
