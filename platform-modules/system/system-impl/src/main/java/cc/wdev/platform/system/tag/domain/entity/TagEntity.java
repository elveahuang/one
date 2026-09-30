package cc.wdev.platform.system.tag.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author irving
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tag")
@Schema(description = "标签实体")
public class TagEntity extends BaseTenantEntity {
    /**
     * 参考Id
     */
    @Schema(title = "参考ID", description = "参考ID")
    private Long referenceId;
    /**
     * 业务类型
     */
    @Schema(title = "标签类型", description = "标签类型")
    private String bizType;
    /**
     * 标签ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    private Long bizId;
    /**
     * 文本
     */
    @Schema(title = "标签文本", description = "标签文本")
    private String title;
    /**
     * 附加信息
     */
    @Schema(title = "标签附加信息", description = "标签附加信息")
    private String extra;
    /**
     * 发布状态
     */
    @Schema(title = "发布状态", description = "发布状态")
    private Integer status;
    /**
     * 数据范围
     */
    @Schema(title = "数据范围", description = "数据范围")
    private Integer scope;
    /**
     * 备注
     */
    @Schema(title = "标签备注", description = "标签备注")
    private String description;
    /**
     * 序号
     */
    @Schema(title = "标签序号", description = "标签序号")
    private Integer idx;
    /**
     * 来源
     */
    @Schema(title = "标签来源", description = "标签来源")
    private Integer source;
}
