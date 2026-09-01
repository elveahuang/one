package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_api_key")
public class AiApiKeyEntity extends BaseTenantEntity {
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * appId
     */
    private String appId;
    /**
     * appName
     */
    private String appName;
    /**
     * appSecret
     */
    private String appSecret;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态
     */
    private Integer status;
}
