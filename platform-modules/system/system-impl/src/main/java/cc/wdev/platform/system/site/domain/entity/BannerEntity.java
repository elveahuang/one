package cc.wdev.platform.system.site.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_banner")
@Schema(description = "宣传栏实体")
public class BannerEntity extends BaseEntity {
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 详情
     */
    @Schema(description = "详情")
    private String details;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 序号
     */
    @Schema(description = "序号")
    private Integer idx;
    /**
     * 类型
     */
    @Schema(description = "类型")
    @TableField(exist = false)
    private RelationVo<?> type;
    /**
     * 封面
     */
    @Schema(description = "封面")
    @TableField(exist = false)
    private AttachmentVo cover;
    /**
     * 移动端封面
     */
    @Schema(description = "移动端封面")
    @TableField(exist = false)
    private AttachmentVo mobileCover;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
