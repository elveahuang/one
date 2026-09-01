package cc.wdev.platform.system.commons.domain.vo;


import cc.wdev.platform.commons.oapis.location.LocationConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@Schema(description = "版本信息")

public class InitializeVo implements Serializable {

    @Schema(description = "是否启用登录验证码")
    private Boolean loginCaptchaEnabled;

    @Schema(description = "访问限制")
    private Boolean accessLimitEnabled;

    @Schema(description = "访问限制类型")
    private String accessLimitType;

    @Schema(description = "访问限制信息")
    private String accessLimitMessage;

    @Schema(description = "应用信息")
    private AppVo app;

    @Schema(description = "位置服务")
    private LocationConfig location;

}
