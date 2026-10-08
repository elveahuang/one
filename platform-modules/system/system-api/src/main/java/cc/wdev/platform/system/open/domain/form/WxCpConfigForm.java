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
 * 企业微信应用配置保存表单
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "企业微信应用配置保存表单")
public class WxCpConfigForm implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 企业微信名称
     */
    @Schema(title = "企业微信名称", description = "企业微信名称")
    private String title;

    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;

    /**
     * 企业微信 CorpId
     */
    @Schema(title = "企业微信 CorpId", description = "企业微信 CorpId")
    private String appId;

    /**
     * 企业微信 AppSecret
     */
    @Schema(title = "企业微信 AppSecret", description = "企业微信 AppSecret")
    private String appSecret;

    /**
     * 企业微信 AES Key
     */
    @Schema(title = "企业微信 AES Key", description = "企业微信 AES Key")
    private String appAesKey;

    /**
     * 企业微信 Token
     */
    @Schema(title = "企业微信 Token", description = "企业微信 Token")
    private String appToken;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

}
