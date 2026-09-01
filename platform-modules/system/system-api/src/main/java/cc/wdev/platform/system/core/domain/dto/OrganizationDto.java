package cc.wdev.platform.system.core.domain.dto;

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
@Schema(description = "组织实体Dto")
public class OrganizationDto implements Serializable {
    @Schema(description = "组织ID")
    private Long id;
    @Schema(description = "组织编码")
    private String code;
    @Schema(description = "组织标题")
    private String title;
    @Schema(description = "组织文本")
    private String label;
    @Schema(description = "组织描述")
    private String description;
}
