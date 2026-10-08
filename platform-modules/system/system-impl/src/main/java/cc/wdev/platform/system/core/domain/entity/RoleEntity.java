package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(title = "角色实体", description = "角色实体")
public class RoleEntity extends BaseTenantEntity {
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
    /**
     * 分组类型
     */
    @Schema(title = "分组类型", description = "分组类型")
    private String groupType;
    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;
    /**
     * 文本
     */
    @Schema(title = "文本", description = "文本")
    private String label;
    /**
     * 名称
     */
    @Schema(title = "名称", description = "名称")
    private String title;
    /**
     * 数据范围
     */
    @Schema(title = "数据范围", description = "数据范围")
    private String dataScopeType;
    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
    /**
     * 来源
     */
    @Schema(title = "来源", description = "来源")
    private Integer source;
    /**
     * 是否为默认角色
     */
    @Schema(title = "是否为默认角色", description = "是否为默认角色")
    private Integer defaultInd;
}
