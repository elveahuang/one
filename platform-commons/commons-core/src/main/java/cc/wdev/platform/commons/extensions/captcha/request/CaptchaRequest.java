package cc.wdev.platform.commons.extensions.captcha.request;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码请求")
public class CaptchaRequest implements Serializable {
    @Schema(description = "验证码类型")
    private CaptchaTypeEnum type;
    @Schema(description = "业务ID")
    @Builder.Default
    private Long bizId = 0L;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "移动国家码")
    private String mobileCountryCode;
    @Schema(description = "移动手机号")
    private String mobileNumber;
    @Schema(description = "验证码长度")
    @Builder.Default
    private int size = 6;
    @Schema(description = "验证码有效期")
    @Builder.Default
    private Duration duration = Duration.ofMinutes(5);
}
