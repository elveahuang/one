package cc.wdev.platform.system.config.domain.form;

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
@Schema(description = "站点基本信息")
public class AppBaseSettingForm implements Serializable {

    @Schema(description = "站点名称")
    private String title;

    @Schema(description = "站点版权")
    private String copyright;

    @Schema(description = "Logo")
    private String logo;

}
