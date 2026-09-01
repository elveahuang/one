package cc.wdev.platform.commons.extensions.captcha.provider;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.commons.utils.StringUtils;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;

/**
 * @author elvea
 */
public class DefaultCodeCaptchaProvider implements CodeCaptchaProvider {

    @Override
    public Captcha generate(CaptchaRequest request) {
        int length = (request.getSize() <= 0) ? 4 : request.getSize();
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(160, 24, length, 16);
        lineCaptcha.getImageBase64();
        return Captcha.builder().type(CaptchaTypeEnum.CODE)
            .key(StringUtils.uuid())
            .value(lineCaptcha.getCode())
            .image(lineCaptcha.getImageBase64Data())
            .build();
    }

}
