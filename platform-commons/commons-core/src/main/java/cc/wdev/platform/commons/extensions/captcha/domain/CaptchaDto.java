package cc.wdev.platform.commons.extensions.captcha.domain;

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
public class CaptchaDto implements Serializable {
    @Schema(description = "验证码key")
    private String key;
}
