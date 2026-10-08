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
 * 钉钉应用配置保存表单
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "钉钉应用配置保存表单")
public class DingtalkConfigForm implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 钉钉名称
     */
    @Schema(title = "钉钉名称", description = "钉钉名称")
    private String title;

    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;

    /**
     * 钉钉 AppId
     */
    @Schema(title = "钉钉 AppId", description = "钉钉 AppId")
    private String appId;

    /**
     * 钉钉 AppSecret
     */
    @Schema(title = "钉钉 AppSecret", description = "钉钉 AppSecret")
    private String appSecret;

    /**
     * 钉钉 AES Key
     */
    @Schema(title = "钉钉 AES Key", description = "钉钉 AES Key")
    private String appAesKey;

    /**
     * 钉钉 Token
     */
    @Schema(title = "钉钉 Token", description = "钉钉 Token")
    private String appToken;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

}
