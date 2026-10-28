package cc.wdev.platform.system.security.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_authorization_consent")
public class AuthorizationConsentEntity extends BaseTenantEntity {
    /**
     * uuid 与 jsqlparser 5.4 关键字冲突，用带引号标识符
     */
    @TableField("\"uuid\"")
    private String uuid;
    /**
     *
     */
    private String clientId;
    /**
     *
     */
    private String principalName;
    /**
     *
     */
    private String authorities;
}
