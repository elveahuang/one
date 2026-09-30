package cc.wdev.platform.system.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@Schema(description = "用户忘记密码VO")
public class UserForgetPasswordVo implements Serializable {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "邮箱")
    private String email;
}
