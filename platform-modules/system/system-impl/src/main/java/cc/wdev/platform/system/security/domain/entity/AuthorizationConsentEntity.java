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
     *
     */
    @TableField("uid")
    private String uid;
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
