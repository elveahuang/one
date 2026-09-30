package cc.wdev.platform.system.config.domain.request;

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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "配置参数保存请求")
public class ConfigGetRequest implements Serializable {
    /**
     * 参数名
     */
    @Schema(description = "参数名")
    private String configKey;
    /**
     * 租户ID
     */
    @Schema(description = "租户")
    private Long tenantId;
}
