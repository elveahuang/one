package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AiToolVo implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;

    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    private String title;

    /**
     * 工具名称
     */
    @Schema(title = "工具名称", description = "工具名称")
    private String toolName;

    /**
     * 备注说明
     */
    @Schema(title = "备注说明", description = "备注说明")
    private String description;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
}
