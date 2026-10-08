package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 知识库向量化任务
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_kb_task")
@Schema(title = "知识库向量化任务", description = "知识库向量化任务")
public class AiKbTaskEntity extends BaseTenantEntity {

    /**
     * 知识库ID
     */
    @Schema(description = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 知识条目ID
     */
    @Schema(description = "知识条目ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbItemId;

    /**
     * 任务标识
     */
    @Schema(description = "任务标识")
    private String taskId;

    /**
     * 任务类型
     */
    @Schema(description = "任务类型")
    private String taskType;

    /**
     * 异常信息
     */
    @Schema(description = "异常信息")
    private String exception;

    /**
     * 消耗词元
     */
    @Schema(description = "消耗词元")
    private Integer tokenUsage;

    /**
     * 进度
     */
    @Schema(description = "进度")
    private Integer progress;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

}
