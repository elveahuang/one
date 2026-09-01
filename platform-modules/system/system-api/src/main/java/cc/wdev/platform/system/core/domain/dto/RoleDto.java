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
@Schema(description = "角色实体Dto")
public class RoleDto implements Serializable {
    /**
     * ID
     */
    @Schema(description = "角色ID")
    private Long id;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 角色编号
     */
    @Schema(description = "角色编码")
    private String code;
    /**
     * 角色编号
     */
    @Schema(description = "角色文本")
    private String label;
    /**
     * 角色标题
     */
    @Schema(description = "角色标题")
    private String title;
    /**
     * 角色数据范围
     */
    @Schema(description = "角色数据范围")
    private String dataScopeType;
}
