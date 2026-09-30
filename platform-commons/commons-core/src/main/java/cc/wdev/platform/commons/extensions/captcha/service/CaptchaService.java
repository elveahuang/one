package cc.wdev.platform.commons.extensions.captcha.service;

import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.provider.CaptchaProvider;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;

/**
 * @author elvea
 */
public interface CaptchaService {

    Captcha generate(CaptchaRequest request) throws Exception;

    /**
     * 检查验证码
     */
    boolean check(CaptchaCheckRequest request);

    /**
     * 检查验证码，不管结果是否成功，检查后都将删除验证码
     */
    boolean validate(CaptchaCheckRequest request);

    CaptchaProvider getCaptchaProvider(CaptchaRequest request);

}
