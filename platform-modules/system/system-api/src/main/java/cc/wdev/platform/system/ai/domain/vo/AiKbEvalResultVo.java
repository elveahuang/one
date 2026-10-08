package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 知识库检索评估结果
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库检索评估结果")
public class AiKbEvalResultVo implements Serializable {

    /**
     * 测试问题
     */
    @Schema(title = "测试问题")
    private String query;

    /**
     * 召回结果
     */
    @Schema(title = "召回结果")
    private List<AiKbSearchResultVo> hits;

    /**
     * 召回条数
     */
    @Schema(title = "召回条数")
    private int hitCount;

}
