package cc.wdev.platform.system.job.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "任务保存请求")
public class JobSaveRequest implements Serializable {
    /**
     * id
     */
    @Schema(description = "任务id")
    private Long id;
    /**
     * 编号
     */
    @Schema(description = "任务编号")
    @NotBlank(message = "编号不能为空")
    private String code;
    /**
     * 线程类名
     */
    @Schema(description = "任务线程类名")
    @NotBlank(message = "类名不能为空")
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
    @Schema(description = "单位")
    private String unit;
    /**
     * 周期
     */
    @Schema(description = "任务周期")
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
     * 状态
     */
    @Schema(description = "任务状态")
    private Integer status;
    /**
     * 参数
     */
    @Schema(description = "任务参数")
    private String params;
}
