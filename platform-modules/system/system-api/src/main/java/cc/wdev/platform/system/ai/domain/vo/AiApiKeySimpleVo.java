package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AiApiKeySimpleVo implements Serializable {

    @Schema(title = "App ID", description = "App ID")
    private String appId;

    @Schema(title = "App Name", description = "App Name")
    private String appName;

    @Schema(title = "App Secret", description = "App Secret")
    private String appSecret;

}
