package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 知识库向量化任务分页查询请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库向量化任务分页查询请求")
public class AiKbTaskSearchRequest extends PageRequest {

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    private Long kbId;

    /**
     * 知识库编号
     */
    @Schema(title = "知识库编号")
    private String kbCode;

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

}
