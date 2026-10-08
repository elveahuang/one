package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI工具查询请求")
public class AiToolSaveRequest implements Serializable {
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
    @NotBlank(message = "编号不能为空")
    private String code;

    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    @NotBlank(message = "名称不能为空")
    private String title;

    /**
     * 工具名称
     */
    @Schema(title = "工具名称", description = "工具名称")
    @NotBlank(message = "工具名称不能为空")
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
