package cc.wdev.platform.commons.extensions.captcha.provider;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class DefaultMailCaptchaProvider implements MailCaptchaProvider {

    @Override
    public Captcha generate(CaptchaRequest request) throws Exception {
        int length = (request.getSize() <= 0) ? 6 : request.getSize();
        String number = StringUtils.randomNumeric(length);
        return Captcha.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(request.getEmail())
            .key(StringUtils.uuid())
            .value(number).build();
    }

}
