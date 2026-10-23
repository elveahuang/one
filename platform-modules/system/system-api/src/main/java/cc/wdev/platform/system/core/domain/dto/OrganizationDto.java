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
@Schema(description = "部门实体Dto")
public class OrganizationDto implements Serializable {
    @Schema(description = "部门ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "父级部门ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @Schema(description = "部门编码")
    private String code;
    @Schema(description = "部门标题")
    private String title;
    @Schema(description = "部门文本")
    private String label;
    @Schema(description = "部门描述")
    private String description;
    @Schema(description = "部门状态")
    private Integer status;
}
