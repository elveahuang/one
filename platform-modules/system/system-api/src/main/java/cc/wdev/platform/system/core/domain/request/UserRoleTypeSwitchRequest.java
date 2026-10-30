package cc.wdev.platform.system.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 切换身份请求
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "切换身份请求")
public class UserRoleTypeSwitchRequest implements Serializable {

    @Schema(description = "目标角色类型", example = "USER")
    @NotBlank(message = "角色类型不能为空")
    private String roleType;

}
