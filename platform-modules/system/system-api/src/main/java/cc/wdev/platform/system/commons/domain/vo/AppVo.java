package cc.wdev.platform.system.commons.domain.vo;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@Schema(description = "站点信息")
public class AppVo implements Serializable {

    @Schema(description = "站点名称")
    private String title;

    @Schema(description = "站点版权")
    private String copyright;

    @Schema(description = "移动端域名")
    private String mobileDomain;

    @Schema(description = "移动端WS域名")
    private String webSocketServer;

    @Builder.Default
    @Schema(description = "版本号")
    private String version = GlobalConstants.VERSION;

    @Schema(description = "微信公众号应用配置信息")
    private WxMpAppVo wxMpApp;

}
