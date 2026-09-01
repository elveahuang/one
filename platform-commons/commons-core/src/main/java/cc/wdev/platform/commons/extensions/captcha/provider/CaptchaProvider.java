package cc.wdev.platform.commons.extensions.captcha.provider;

import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;

/**
 * @author elvea
 */
public interface CaptchaProvider {

    Captcha generate(CaptchaRequest request) throws Exception;

}
