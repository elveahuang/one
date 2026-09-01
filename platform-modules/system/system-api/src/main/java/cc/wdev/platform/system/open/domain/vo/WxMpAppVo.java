package cc.wdev.platform.system.open.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信公众号应用配置信息Vo")
public class WxMpAppVo implements Serializable {
    /**
     * AppId
     */
    @Schema(title = "AppId", description = "AppId")
    private String appId;
}
