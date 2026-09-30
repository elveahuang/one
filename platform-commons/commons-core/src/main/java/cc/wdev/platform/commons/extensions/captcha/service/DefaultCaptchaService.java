package cc.wdev.platform.commons.extensions.captcha.service;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.provider.CaptchaProvider;
import cc.wdev.platform.commons.extensions.captcha.provider.CodeCaptchaProvider;
import cc.wdev.platform.commons.extensions.captcha.provider.MailCaptchaProvider;
import cc.wdev.platform.commons.extensions.captcha.provider.SmsCaptchaProvider;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.commons.extensions.captcha.store.CaptchaStore;
import cc.wdev.platform.commons.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultCaptchaService implements CaptchaService {

    private MailCaptchaProvider mailCaptchaProvider;

    private SmsCaptchaProvider smsCaptchaProvider;

    private CodeCaptchaProvider codeCaptchaProvider;

    private CaptchaStore captchaStore;

    @Override
    public Captcha generate(CaptchaRequest request) throws Exception {
        Captcha captcha = getCaptchaProvider(request).generate(request);
        captchaStore.set(captcha.getKey(), captcha, request.getDuration());
        return captcha;
    }

    @Override
    public boolean check(CaptchaCheckRequest request) {
        Captcha captcha = captchaStore.get(request.getKey());

        boolean result = false;
        if (CaptchaTypeEnum.EMAIL.equals(request.getType())) {
            result = (!ObjectUtils.isEmpty(captcha)
                && captcha.getEmail().equalsIgnoreCase(request.getEmail())
                && captcha.getValue().equalsIgnoreCase(request.getValue()));
        } else if (CaptchaTypeEnum.SMS.equals(request.getType())) {
            result = (!ObjectUtils.isEmpty(captcha)
                && captcha.getMobileNumber().equalsIgnoreCase(request.getMobileNumber())
                && captcha.getValue().equalsIgnoreCase(request.getValue()));
        } else if (CaptchaTypeEnum.CODE.equals(request.getType())) {
            result = (!ObjectUtils.isEmpty(captcha) && captcha.getValue().equalsIgnoreCase(request.getValue()));
        }
        return result;
    }

    @Override
    public boolean validate(CaptchaCheckRequest request) {
        boolean result = check(request);
        captchaStore.remove(request.getKey());
        return result;
    }

    @Override
    public CaptchaProvider getCaptchaProvider(CaptchaRequest request) {
        if (CaptchaTypeEnum.SMS.equals(request.getType())) {
            return this.smsCaptchaProvider;
        } else if (CaptchaTypeEnum.EMAIL.equals(request.getType())) {
            return this.mailCaptchaProvider;
        } else if (CaptchaTypeEnum.CODE.equals(request.getType())) {
            return this.codeCaptchaProvider;
        }
        return this.codeCaptchaProvider;
    }

}
