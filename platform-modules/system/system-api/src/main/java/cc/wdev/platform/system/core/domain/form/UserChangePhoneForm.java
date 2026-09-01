package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "账号修改手机号表单")
public class UserChangePhoneForm implements Serializable {
    @Schema(description = "手机区号")
    @NotEmpty
    private String mobileCountryCode;
    @Schema(description = "手机号")
    @NotEmpty
    private String mobileNumber;
    @Schema(description = "验证码KEY")
    @NotEmpty
    private String captchaKey;
    @Schema(description = "验证码VALUE")
    @NotEmpty
    private String captchaValue;
}
