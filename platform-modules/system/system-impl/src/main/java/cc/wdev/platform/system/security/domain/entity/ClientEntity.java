package cc.wdev.platform.system.security.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@TableName("sys_client")
@Schema(description = "客户端实体")
public class ClientEntity extends BaseEntity {
    private String clientId;

    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @Schema(description = "客户端ID签发时间")
    private LocalDateTime clientIdIssuedAt;

    @Schema(description = "客户端密钥")
    private String clientSecret;

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
    @Schema(description = "注销后重定向URI")
    private String postLogoutRedirectUris;
    @Schema(description = "重定向URI")
    private String redirectUris;
    @Schema(description = "作用域")
    private String scopes;
    @Schema(description = "客户端设置")
    private String clientSettings;
    @Schema(description = "令牌设置")
    private String tokenSettings;
    @Schema(description = "客户端描述")
    private String description;
}
