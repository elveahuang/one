package cc.wdev.platform.commons.extensions.captcha.request;

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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码校验请求")
public class CaptchaCheckRequest implements Serializable {
    @Schema(description = "验证码类型")
    private CaptchaTypeEnum type;
    @Schema(description = "电子邮件地址")
    private String email;
    @Schema(description = "移动国家码")
    private String mobileCountryCode;
    @Schema(description = "移动电话号码")
    private String mobileNumber;
    @Schema(description = "验证码键")
    private String key;
    @Schema(description = "验证码值")
    private String value;
}
