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
 * 飞书应用配置保存表单
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "飞书应用配置保存表单")
public class LarkConfigForm implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 飞书名称
     */
    @Schema(title = "飞书名称", description = "飞书名称")
    private String title;

    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;

    /**
     * 飞书 AppId
     */
    @Schema(title = "飞书 AppId", description = "飞书 AppId")
    private String appId;

    /**
     * 飞书 AppSecret
     */
    @Schema(title = "飞书 AppSecret", description = "飞书 AppSecret")
    private String appSecret;

    /**
     * 飞书 AES Key
     */
    @Schema(title = "飞书 AES Key", description = "飞书 AES Key")
    private String appAesKey;

    /**
     * 飞书 Token
     */
    @Schema(title = "飞书 Token", description = "飞书 Token")
    private String appToken;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

}
