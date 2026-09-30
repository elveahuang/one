package cc.wdev.platform.system.open.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 飞书应用信息Vo
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "飞书应用信息Vo")
public class LarkAppVo implements Serializable {

    /**
     * AppId
     */
    @Schema(title = "AppId", description = "AppId")
    private String appId;

}
