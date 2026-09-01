package cc.wdev.platform.system.security.domain.dto;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "授权DTO")
public class AuthorizationDto implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "UUID")
    private String uuid;
    @Schema(description = "客户端ID")
    private String clientId;
    @Schema(description = "主体名称")
    private String principalName;
    @Schema(description = "授权类型")
    private String authorizationGrantType;
    @Schema(description = "属性")
    private String attributes;
    @Schema(description = "状态")
    private String state;

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * Authorization Code
     * -----------------------------------------------------------------------------------------------------------------
     */
    @Schema(description = "授权码值")
    private String authorizationCodeValue;
    @Schema(description = "授权码元数据")
    private String authorizationCodeMetadata;
    @Schema(description = "授权码创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime authorizationCodeIssuedAt;
    @Schema(description = "授权码过期时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime authorizationCodeExpiresAt;

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * OIDC ID Token
     * -----------------------------------------------------------------------------------------------------------------
     */
    @Schema(description = "访问令牌值")
    private String accessTokenValue;
    @Schema(description = "访问令牌元数据")
    private String accessTokenMetadata;
    @Schema(description = "访问令牌类型")
    private String accessTokenType;
    @Schema(description = "访问令牌作用域")
    private String accessTokenScopes;
    @Schema(description = "访问令牌创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime accessTokenIssuedAt;
    @Schema(description = "访问令牌过期时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime accessTokenExpiresAt;

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * OIDC ID Token
     * -----------------------------------------------------------------------------------------------------------------
     */
    @Schema(description = "OIDC ID令牌值")
    private String oidcIdTokenValue;
    @Schema(description = "OIDC ID令牌元数据")
    private String oidcIdTokenMetadata;
    @Schema(description = "OIDC ID令牌声明")
    private String oidcIdTokenClaims;
    @Schema(description = "OIDC ID令牌创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime oidcIdTokenIssuedAt;
    @Schema(description = "OIDC ID令牌过期时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime oidcIdTokenExpiresAt;

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * Refresh Token
     * -----------------------------------------------------------------------------------------------------------------
     */
    @Schema(description = "刷新令牌值")
    private String refreshTokenValue;
    @Schema(description = "刷新令牌元数据")
    private String refreshTokenMetadata;
    @Schema(description = "刷新令牌创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime refreshTokenIssuedAt;
    @Schema(description = "刷新令牌过期时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime refreshTokenExpiresAt;

}
