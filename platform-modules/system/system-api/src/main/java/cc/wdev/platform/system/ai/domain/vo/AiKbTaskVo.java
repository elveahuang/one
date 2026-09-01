package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库向量化任务")
public class AiKbTaskVo implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 知识条目ID
     */
    @Schema(title = "知识条目ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbItemId;

    /**
     * 任务类型
     */
    @Schema(title = "任务类型")
    private String taskType;

    /**
     * 状态
     */
    @Schema(title = "状态")
    private Integer status;

    /**
     * 异常信息
     */
    @Schema(title = "异常信息")
    private String exception;

}
