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
 * 知识分片 VO
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识分片 VO")
public class AiKbChunkVo implements Serializable {

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
     * 业务类型
     */
    @Schema(title = "业务类型")
    private String bizType;

    /**
     * 标题
     */
    @Schema(title = "标题")
    private String title;

    /**
     * 分片序号
     */
    @Schema(title = "分片序号")
    private Integer chunkIndex;

    /**
     * 总分片数
     */
    @Schema(title = "总分片数")
    private Integer chunkTotal;

    /**
     * 分片在原文中的起始位置
     */
    @Schema(title = "分片在原文中的起始位置")
    private Integer startIndex;

    /**
     * 分片在原文中的结束位置
     */
    @Schema(title = "分片在原文中的结束位置")
    private Integer endIndex;

    /**
     * 分片内容
     */
    @Schema(title = "分片内容")
    private String content;

    /**
     * 内容类型
     */
    @Schema(title = "内容类型")
    private String contentType;

    /**
     * 内容大小
     */
    @Schema(title = "内容大小")
    private Long contentSize;

    /**
     * 内容哈希
     */
    @Schema(title = "内容哈希")
    private String contentHash;

    /**
     * 切分策略
     */
    @Schema(title = "切分策略")
    private String chunkStrategy;

    /**
     * 嵌入模型标识
     */
    @Schema(title = "嵌入模型标识")
    private String embeddingModel;

    /**
     * 向量文档ID
     */
    @Schema(title = "向量文档ID")
    private String vectorDocId;

    /**
     * 是否已向量化
     */
    @Schema(title = "是否已向量化")
    private Integer vectorized;

    /**
     * 元数据
     */
    @Schema(title = "元数据")
    private String metadata;

    /**
     * 状态
     */
    @Schema(title = "状态")
    private Integer status;

}
