package cc.wdev.platform.system.open.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 微信小程序应用配置信息Vo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信小程序应用配置信息Vo")
public class WxMaAppVo implements Serializable {
    /**
     * 小程序 AppId
     */
    @Schema(title = "小程序 AppId", description = "小程序 AppId")
    private String appId;
}
