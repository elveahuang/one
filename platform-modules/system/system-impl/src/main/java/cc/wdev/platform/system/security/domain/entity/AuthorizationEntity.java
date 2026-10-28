package cc.wdev.platform.system.security.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_authorization")
public class AuthorizationEntity extends BaseTenantEntity {
    /**
     * uuid 与 jsqlparser 5.4 关键字冲突，用带引号标识符
     */
    @TableField("\"uuid\"")
    private String uuid;
    /**
     *
     */
    private String clientId;
    private String principalName;
    private String authorizationGrantType;
    private String attributes;
    /**
     *
     */
    private String state;
    /**
     *
     */
    private String authorizationCodeValue;
    private LocalDateTime authorizationCodeIssuedAt;
    private LocalDateTime authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;
    /**
     *
     */
    private String accessTokenValue;
    private LocalDateTime accessTokenIssuedAt;
    private LocalDateTime accessTokenExpiresAt;
    private String accessTokenMetadata;
    private String accessTokenType;
    private String accessTokenScopes;
    /**
     *
     */
    private String oidcIdTokenValue;
    private LocalDateTime oidcIdTokenIssuedAt;
    private LocalDateTime oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String oidcIdTokenClaims;
    /**
     *
     */
    private String refreshTokenValue;
    private LocalDateTime refreshTokenIssuedAt;
    private LocalDateTime refreshTokenExpiresAt;
    private String refreshTokenMetadata;
}
