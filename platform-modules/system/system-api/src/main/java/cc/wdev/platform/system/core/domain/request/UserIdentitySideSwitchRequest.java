package cc.wdev.platform.system.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 切换身份端请求
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "切换身份端请求")
public class UserIdentitySideSwitchRequest implements Serializable {

    @Schema(description = "目标身份端（ADMIN / MEMBER）", example = "MEMBER")
    @NotBlank(message = "身份端不能为空")
    private String side;

}
