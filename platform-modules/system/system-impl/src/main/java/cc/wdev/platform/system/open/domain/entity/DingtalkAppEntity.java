package cc.wdev.platform.system.open.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 钉钉应用实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_open_dingtalk_app")
public class DingtalkAppEntity extends BaseTenantEntity {

    /**
     * 公众号名称
     */
    private String title;

    /**
     * AppID
     */
    private String appId;

    /**
     * AppSecret
     */
    private String appSecret;

    /**
     * Token
     */
    private String appToken;

    /**
     * EncodingAESKey
     */
    private String appAesKey;

    /**
     * 备注
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 启用状态
     */
    private Integer active;

}
