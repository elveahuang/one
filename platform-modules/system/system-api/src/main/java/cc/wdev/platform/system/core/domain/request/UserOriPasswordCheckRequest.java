package cc.wdev.platform.system.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "账号原密码检查请求")
public class UserOriPasswordCheckRequest {
    @Schema(description = "原密码")
    private String oriPassword;
}
