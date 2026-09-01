package cc.wdev.platform.system.security.domain.dto;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

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
@Schema(description = "客户端DTO")
public class ClientDto implements Serializable {
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "客户端ID")
    private String clientId;
    @Schema(description = "客户端密钥")
    private String clientSecret;
    @Schema(description = "客户端ID创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime clientIdIssuedAt;
    @Schema(description = "客户端密钥过期时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime clientSecretExpiresAt;
    @Schema(description = "客户端名称")
    private String clientName;
    @Schema(description = "客户端认证方法")
    private String clientAuthenticationMethods;
    @Schema(description = "授权类型")
    private String authorizationGrantTypes;
    @Schema(description = "重定向URI")
    private String redirectUris;
    @Schema(description = "注销后重定向URI")
    private String postLogoutRedirectUris;
    @Schema(description = "作用域")
    private String scopes;
    @Schema(description = "客户端设置")
    private String clientSettings;
    @Schema(description = "令牌设置")
    private String tokenSettings;
}
