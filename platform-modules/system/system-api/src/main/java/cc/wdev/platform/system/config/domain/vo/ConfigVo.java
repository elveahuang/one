package cc.wdev.platform.system.config.domain.vo;

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
@Schema(description = "配置Vo")
public class ConfigVo implements Serializable {
    @Schema(description = "配置分组类型")
    private String configGroupType;
    @Schema(description = "配置内容类型")
    private String configContentType;
    @Schema(description = "配置键")
    private String configKey;
    @Schema(description = "配置值")
    private String configValue;
}
