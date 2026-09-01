package cc.wdev.platform.system.open.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 微信公众号应用
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_open_wxmp_app")
@Schema(title = "微信公众号应用", description = "微信公众号应用")
public class WxMpAppEntity extends BaseTenantEntity {

    /**
     * 公众号名称
     */
    @Schema(title = "公众号名称", description = "公众号名称")
    private String title;

    /**
     * AppID
     */
    @Schema(title = "AppID", description = "AppID")
    private String appId;

    /**
     * AppSecret
     */
    @Schema(title = "AppSecret", description = "AppSecret")
    private String appSecret;

    /**
     * Token
     */
    @Schema(title = "AppToken", description = "AppToken")
    private String appToken;

    /**
     * EncodingAESKey
     */
    @Schema(title = "EncodingAESKey", description = "EncodingAESKey")
    private String appAesKey;

    /**
     * MPID
     */
    @Schema(title = "MPID", description = "MPID")
    private String appMpId;

    /**
     * WXID
     */
    @Schema(title = "WXID", description = "WXID")
    private String appWxId;

    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

    /**
     * 来源
     */
    @Schema(title = "来源", description = "来源")
    private Integer source;

    /**
     * 启用状态
     */
    @Schema(title = "启用状态", description = "启用状态")
    private Integer active;

}
