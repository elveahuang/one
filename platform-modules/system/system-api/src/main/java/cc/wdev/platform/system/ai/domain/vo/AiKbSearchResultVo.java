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
 * 知识库检索结果VO
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库检索结果VO")
public class AiKbSearchResultVo implements Serializable {

    /**
     * 来源（附件ID / URL / 文件名）
     */
    @Schema(title = "来源", description = "来源（附件ID / URL / 文件名）")
    private String source;

    /**
     * 内容类型
     */
    @Schema(title = "内容类型", description = "内容类型")
    private String contentType;

    /**
     * 相似度得分
     */
    @Schema(title = "相似度得分", description = "相似度得分")
    private Double score;

    /**
     * 内容
     */
    @Schema(title = "内容", description = "内容")
    private String content;

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID", description = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 知识条目ID（溯源定位用）
     */
    @Schema(title = "知识条目ID", description = "知识条目ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbItemId;

    /**
     * 分片ID（溯源定位用）
     */
    @Schema(title = "分片ID", description = "分片ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chunkId;

    /**
     * 分片标题
     */
    @Schema(title = "分片标题", description = "分片标题")
    private String title;

    /**
     * 分片在原文中的起始偏移
     */
    @Schema(title = "起始偏移", description = "分片在原文中的起始偏移")
    private Integer startOffset;

    /**
     * 分片在原文中的结束偏移
     */
    @Schema(title = "结束偏移", description = "分片在原文中的结束偏移")
    private Integer endOffset;

    /**
     * 分片序号
     */
    @Schema(title = "分片序号", description = "分片序号")
    private Integer chunkIndex;

    /**
     * 检索类型（VECTOR / BM25 / HYBRID），用于区分 score 语义
     */
    @Schema(title = "检索类型", description = "检索类型（VECTOR / BM25 / HYBRID）")
    private String scoreType;

}
