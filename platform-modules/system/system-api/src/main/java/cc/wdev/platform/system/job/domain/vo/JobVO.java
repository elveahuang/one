package cc.wdev.platform.system.job.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Belly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "任务VO")
public class JobVO implements Serializable {
    /**
     * id
     */
    @Schema(description = "任务id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 编号
     */
    @Schema(description = "任务编号")
    private String code;
    /**
     * 线程类名
     */
    @Schema(description = "任务线程类名")
    private String classname;
    /**
     * 描述说明
     */
    @Schema(description = "任务描述说明")
    private String description;
    /**
     * 类型
     */
    @Schema(description = "任务类型")
    private String type;
    /**
     * 单位
     */
    @Schema(description = "任务单位")
    private String unit;
    /**
     * 周期
     */
    @Schema(description = "周期")
    private Integer period;
    /**
     * 小时
     */
    @Schema(description = "小时")
    private Integer hour;
    /**
     * 分钟
     */
    @Schema(description = "分钟")
    private Integer minute;
    /**
     * 表达式
     */
    @Schema(description = "表达式")
    private String cron;
    /**
     * 参数
     */
    @Schema(description = "任务参数")
    private String params;
    /**
     * 状态
     */
    @Schema(description = "任务状态")
    private Integer status;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private Long createdBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createdAt;
    /**
     * 最后修改人
     */
    @Schema(description = "最后修改人")
    private Long updatedBy;
    /**
     * 最后修改时间
     */
    @Schema(description = "最后修改时间")
    private Date updatedAt;
}
