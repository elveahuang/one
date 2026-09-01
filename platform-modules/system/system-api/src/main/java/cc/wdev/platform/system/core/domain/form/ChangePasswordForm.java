package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "修改密码表单")
public class ChangePasswordForm {
    @Schema(description = "原始密码")
    @NotEmpty
    private String originalPassword;
    @Schema(description = "新密码")
    @NotEmpty
    private String newPassword;
}
