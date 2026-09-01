package cc.wdev.platform.system.tag.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * @author irving
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "标签保存请求", description = "标签保存请求")
public class TagSaveRequest implements Serializable {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;
    /**
     * 标签业务类型
     */
    @Schema(title = "业务类型", description = "标签业务类型")
    private String bizType;
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    private Long bizId;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    private List<Long> bizIdList;
    /**
     * 文本
     */
    @NotBlank(message = "标题不能为空")
    @Schema(title = "标题", description = "标题")
    @Size(max = 150, message = "文本长度不能超过150个字符")
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
