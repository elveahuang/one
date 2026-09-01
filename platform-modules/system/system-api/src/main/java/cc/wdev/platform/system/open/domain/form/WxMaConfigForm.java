package cc.wdev.platform.system.open.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 微信小程序应用配置保存表单
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信小程序应用配置保存表单")
public class WxMaConfigForm implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 小程序名称
     */
    @Schema(title = "小程序名称", description = "小程序名称")
    private String title;

    /**
     * 小程序备注
     */
    @Schema(title = "小程序备注", description = "小程序备注")
    private String description;

    /**
     * 小程序 AppId
     */
    @Schema(title = "小程序 AppId", description = "小程序 AppId")
    private String appId;

    /**
     * 小程序 AppSecret
     */
    @Schema(title = "小程序 AppSecret", description = "小程序 AppSecret")
    private String appSecret;

    /**
     * 小程序 AES Key
     */
    @Schema(title = "小程序 AES Key", description = "小程序 AES Key")
    private String appAesKey;

    /**
     * 小程序 Token
     */
    @Schema(title = "小程序 Token", description = "小程序 Token")
    private String appToken;

    /**
     * 小程序wxID
     */
    @Schema(title = "小程序wxID", description = "小程序wxID")
    private String appWxId;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

}
