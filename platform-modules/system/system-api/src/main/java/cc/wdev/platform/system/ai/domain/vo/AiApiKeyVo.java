package cc.wdev.platform.system.ai.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.annotations.SensitiveMark;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AiApiKeyVo implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
    /**
     * 描述
     */
    @Schema(title = "描述", description = "描述")
    private String description;
    /**
     * App ID
     */
    @Schema(title = "App ID", description = "App ID")
    private String appId;
    /**
     * App Name
     */
    @Schema(title = "App Name", description = "App Name")
    private String appName;
    /**
     * App Secret
     */
    @Schema(title = "App Secret", description = "App Secret")
    @SensitiveMark(strategy = SensitiveMarkStrategy.API_KEY)
    private String appSecret;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
    /**
     * 最后修改时间
     */
    @Schema(title = "最后修改时间", description = "最后修改时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

}
