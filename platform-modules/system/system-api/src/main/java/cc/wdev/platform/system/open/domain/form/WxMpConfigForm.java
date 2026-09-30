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
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信公众号应用配置")
public class WxMpConfigForm implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 公众号名称
     */
    @Schema(title = "公众号名称", description = "公众号名称")
    private String title;
    /**
     * 公众号备注
     */
    @Schema(title = "公众号备注", description = "公众号备注")
    private String description;
    /**
     * 公众号 AppId
     */
    @Schema(title = "公众号 AppId", description = "公众号 AppId")
    private String appId;
    /**
     * 公众号 AppSecret
     */
    @Schema(title = "公众号 AppSecret", description = "公众号 AppSecret")
    private String appSecret;
    /**
     * 公众号 AES Key
     */
    @Schema(title = "公众号 AES Key", description = "公众号 AES Key")
    private String appAesKey;
    /**
     * 公众号 Token
     */
    @Schema(title = "公众号 Token", description = "公众号 Token")
    private String appToken;
    /**
     * 公众号 MP ID
     */
    @Schema(title = "公众号 MP ID", description = "公众号 MP ID")
    private String appMpId;

    /**
     * 公众号 WX ID
     */
    @Schema(title = "公众号 WX ID", description = "公众号 WX ID")
    private String appWxId;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
}
