package cc.wdev.platform.commons.extensions.captcha.domain;

import lombok.*;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CaptchaLogDto implements Serializable {
    /**
     * 验证码类型
     */
    private String captchaType;
    /**
     * 验证码标识
     */
    private String captchaKey;
    /**
     * 验证码
     */
    private String captchaValue;
    /**
     * 接收人
     */
    private String email;
    /**
     * 国家地区号码
     */
    private String mobileCountryCode;
    /**
     * 手机号码
     */
    private String mobileNumber;
}
