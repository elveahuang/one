package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * 知识库检索评估请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库检索评估请求")
public class AiKbEvalRequest implements Serializable {

    /**
     * 知识库ID
     */
    @NotNull(message = "知识库ID不能为空")
    @Schema(title = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 测试问题集
     */
    @NotEmpty(message = "问题集不能为空")
    @Schema(title = "测试问题集")
    private List<String> queries;

    /**
     * 返回条数
     */
    @Schema(title = "返回条数")
    private Integer topK;

    /**
     * 相似度阈值
     */
    @Schema(title = "相似度阈值")
    private Double similarityThreshold;

}
