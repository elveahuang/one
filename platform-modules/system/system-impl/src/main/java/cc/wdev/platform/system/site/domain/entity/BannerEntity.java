package cc.wdev.platform.system.site.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

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
     * 链接类型（JOB/EXTERNAL）
     */
    @Schema(description = "链接类型")
    private String linkType;
    /**
     * 链接参数（JSON）
     */
    @Schema(description = "链接参数")
    private String linkParams;
    /**
     * 外部链接地址
     */
    @Schema(description = "外部链接地址")
    private String linkExternalUrl;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startDatetime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endDatetime;
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
