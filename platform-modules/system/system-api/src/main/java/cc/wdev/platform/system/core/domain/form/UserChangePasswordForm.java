package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "账号修改密码表单")
public class UserChangePasswordForm {

    @Schema(description = "原始密码")
    private String oriPassword;

    @Schema(description = "新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;
}
