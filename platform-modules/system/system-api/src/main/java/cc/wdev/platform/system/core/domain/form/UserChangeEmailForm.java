package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "账号修改邮箱表单")
public class UserChangeEmailForm implements Serializable {
    @Schema(description = "邮箱")
    @NotEmpty
    private String email;
    @Schema(description = "验证码KEY")
    @NotEmpty
    private String captchaKey;
    @Schema(description = "验证码VALUE")
    @NotEmpty
    private String captchaValue;
}
