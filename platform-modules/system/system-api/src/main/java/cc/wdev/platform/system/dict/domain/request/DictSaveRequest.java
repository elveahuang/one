package cc.wdev.platform.system.dict.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@Schema(title = "字典保存请求", description = "字典保存请求")
public class DictSaveRequest implements Serializable {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;
    /**
     * 字典业务类型
     */
    @Schema(title = "字典业务类型", description = "字典业务类型")
    private String bizType;
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
     * 文本
     */
    @Schema(title = "文本", description = "文本")
    @NotBlank(message = "字典文本不能为空")
    private String title;
    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;
    /**
     * 序号
     */
    @Schema(title = "序号", description = "序号")
    private Integer idx;
    /**
     * 来源
     */
    @Schema(title = "来源", description = "来源")
    private Integer source;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
}
