package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色表单")
public class RoleForm implements Serializable {
    /**
     * ID
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 分组类型
     */
    @Schema(description = "分组类型")
    private String groupType;
    /**
     * 标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "角色名称不能为空")
    private String title;
    /**
     * 文本
     */
    @Schema(description = "文本")
    private String label;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 来源
     */
    @Schema(description = "来源")
    private Integer source;
    /**
     * 权限ID
     */
    @Schema(description = "权限ID")
    private List<Long> authorityIds;
    /**
     * 数据权限
     */
    @Schema(description = "数据权限")
    private String dataScopeType;
    /**
     * 是否为默认角色
     */
    @Schema(title = "是否为默认角色", description = "是否为默认角色")
    private Integer defaultInd;
}
