package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Map;

/**
 * 知识库统计 VO
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库统计 VO")
public class AiKbStatsVo implements Serializable {

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 条目总数
     */
    @Schema(title = "条目总数")
    private Long itemCount;

    /**
     * 分片总数
     */
    @Schema(title = "分片总数")
    private Long chunkCount;

    /**
     * 已向量化条目数
     */
    @Schema(title = "已向量化条目数")
    private Long vectorizedItemCount;

    /**
     * 待向量化条目数
     */
    @Schema(title = "待向量化条目数")
    private Long pendingItemCount;

    /**
     * 失败条目数
     */
    @Schema(title = "失败条目数")
    private Long failedItemCount;

    /**
     * 各业务类型条目数
     */
    @Schema(title = "各业务类型条目数")
    private Map<String, Long> bizTypeCounts;

    /**
     * 失败任务数
     */
    @Schema(title = "失败任务数")
    private Long failedTaskCount;

}
