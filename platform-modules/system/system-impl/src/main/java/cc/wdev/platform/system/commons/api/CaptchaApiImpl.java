package cc.wdev.platform.system.commons.api;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaLogDto;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.commons.extensions.captcha.service.CaptchaService;
import cc.wdev.platform.system.log.api.LogApi;
import cc.wdev.platform.system.message.api.MessageApi;
import cc.wdev.platform.system.message.domain.dto.CreateMessageDto;
import cc.wdev.platform.system.message.enums.MessageChannelEnum;
import cc.wdev.platform.system.message.enums.MessageTargetTypeEnum;
import cc.wdev.platform.system.message.enums.MessageTypeEnum;
import cc.wdev.platform.system.message.utils.MessageBuilder;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author elvea
 */
@Service
@Primary
@AllArgsConstructor
public class CaptchaApiImpl implements CaptchaApi {

    private final CaptchaService captchaService;

    private final LogApi logApi;

    private final MessageApi messageApi;

    /**
     * @see CaptchaApi#generate(CaptchaRequest)
     */
    @Override
    public Captcha generate(CaptchaRequest request) throws Exception {
        Captcha captcha = this.captchaService.generate(request);
        if (CaptchaTypeEnum.SMS.equals(captcha.getType())) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("code", captcha.getValue());
            CreateMessageDto message = MessageBuilder.builder()
                .bizId(request.getBizId())
                .type(MessageTypeEnum.CAPTCHA.getValue())
                .templateType(MessageChannelEnum.SMS)
                .targetType(MessageTargetTypeEnum.IMMEDIATE)
                .sender(1L)
                .recipient(request.getMobileCountryCode(), request.getMobileNumber())
                .params(params)
                .build();
            this.messageApi.createMessage(message);
        } else if (CaptchaTypeEnum.EMAIL.equals(captcha.getType())) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("code", captcha.getValue());
            CreateMessageDto message = MessageBuilder.builder()
                .bizId(request.getBizId())
                .type(MessageTypeEnum.CAPTCHA.getValue())
                .templateType(MessageChannelEnum.MAIL)
                .targetType(MessageTargetTypeEnum.IMMEDIATE)
                .sender(1L)
                .recipient(request.getEmail())
                .params(params)
                .build();
            this.messageApi.createMessage(message);
        }
        // 保存验证码日志
        // 只有短信和邮件验证码才需要保存验证码日志，页面普通的图形验证码无需保存
        if (CaptchaTypeEnum.SMS.equals(captcha.getType()) || CaptchaTypeEnum.EMAIL.equals(captcha.getType())) {
            CaptchaLogDto log = CaptchaLogDto.builder()
                .captchaType(captcha.getType().getValue())
                .captchaKey(captcha.getKey())
                .captchaValue(captcha.getValue())
                .email(captcha.getEmail())
                .mobileCountryCode(captcha.getMobileCountryCode())
                .mobileNumber(captcha.getMobileNumber())
                .build();
            this.logApi.saveCaptchaLog(log);
        }
        return captcha;
    }

    /**
     * @see CaptchaApi#check(CaptchaCheckRequest)
     */
    @Override
    public boolean check(CaptchaCheckRequest request) {
        return this.captchaService.check(request);
    }

}
