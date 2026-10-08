package cc.wdev.platform.system.log.domain.entity;

import cc.wdev.platform.commons.data.jpa.domain.SimpleEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_captcha_log")
public class CaptchaLogEntity extends SimpleEntity {
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
     * 邮箱
     */
    private String email;
    /**
     * 手机号码
     */
    private String mobileCountryCode;
    private String mobileNumber;
}
