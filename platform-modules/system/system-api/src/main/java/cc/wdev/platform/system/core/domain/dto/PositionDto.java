package cc.wdev.platform.system.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

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
@Schema(description = "岗位实体Dto")
public class PositionDto implements Serializable {
    @Schema(description = "岗位ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "父级岗位ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @Schema(description = "岗位编码")
    private String code;
    @Schema(description = "岗位标题")
    private String title;
    @Schema(description = "岗位描述")
    private String description;
    @Schema(description = "岗位状态")
    private Integer status;
}
