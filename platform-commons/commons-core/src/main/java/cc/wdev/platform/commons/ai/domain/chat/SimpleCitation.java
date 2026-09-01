package cc.wdev.platform.commons.ai.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 对话引用（溯源信息）
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话引用")
public class SimpleCitation implements Serializable {

    @Schema(description = "知识条目ID")
    private Long kbItemId;

    @Schema(description = "分片ID")
    private Long chunkId;

    @Schema(description = "分片序号")
    private Integer chunkIndex;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "内容片段")
    private String content;

    @Schema(description = "相似度得分")
    private Double score;

}
