package cc.wdev.platform.commons.oapis.location.tianditu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "天地图配置")
public class TiandituConfig {
    @Schema(description = "token")
    private String token;
}
