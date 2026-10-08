package cc.wdev.platform.commons.extensions.captcha;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "验证码")
public class Captcha implements Serializable {
    @Schema(description = "验证码类型")
    private CaptchaTypeEnum type;
    @Schema(description = "验证码键")
    private String key;
    @Schema(description = "验证码值")
    private String value;
    @Schema(description = "验证码图片")
    private String image;
    @Schema(description = "电子邮件地址")
    private String email;
    @Schema(description = "移动国家代码")
    private String mobileCountryCode;
    @Schema(description = "移动电话号码")
    private String mobileNumber;
}
