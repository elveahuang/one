package cc.wdev.platform.system.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Schema(description = "岗位保存Dto")
public class PositionSaveDto implements Serializable {

    @Schema(description = "岗位ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "父级岗位ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @Schema(description = "岗位编号")
    @Size(max = 150, message = "岗位编号长度不能超过150个字符")
    private String code;

    @Schema(description = "岗位标题")
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 150, message = "岗位名称长度不能超过150个字符")
    private String title;

    @Schema(description = "岗位文本")
    @Size(max = 250, message = "岗位文本长度不能超过250个字符")
    private String label;

    @Schema(description = "岗位描述")
    @Size(max = 255, message = "岗位描述长度不能超过255个字符")
    private String description;

    @Schema(description = "岗位状态")
    private Integer status;

}
