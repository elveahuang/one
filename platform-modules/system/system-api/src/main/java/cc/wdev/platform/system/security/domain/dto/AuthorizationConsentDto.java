package cc.wdev.platform.system.security.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@Schema(description = "授权同意DTO")
public class AuthorizationConsentDto implements Serializable {
    /**
     *
     */
    @Schema(description = "UUID")
    private String uuid;
    /**
     *
     */
    @Schema(description = "客户端ID")
    private String clientId;
    /**
     *
     */
    @Schema(description = "主体名称")
    private String principalName;
    /**
     *
     */
    @Schema(description = "授权范围")
    private String authorities;
}
