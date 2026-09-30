package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_kb_chunk")
@Schema(title = "知识库知识分片", description = "知识库知识分片")
public class AiKbChunkEntity extends BaseTenantEntity {
    /**
     * 知识库ID
     */
    @Schema(description = "知识库ID")
    private Long kbId;
    /**
     * 知识条目ID
     */
    @Schema(description = "知识条目ID")
    private Long kbItemId;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 分片文本内容
     */
    @Schema(description = "分片文本内容")
    private String content;
    /**
     * 内容类型
     */
    @Schema(description = "内容类型")
    private String contentType;
    /**
     * 分片文本大小
     */
    @Schema(description = "分片大小")
    private Long contentSize;
    /**
     * 内容哈希
     */
    @Schema(description = "内容哈希")
    private String contentHash;
    /**
     * 切分策略
     */
    @Schema(description = "切分策略")
    private String chunkStrategy;
    /**
     * 分片序号
     */
    @Schema(description = "分片序号")
    private Integer chunkIndex;
    /**
     * 分片总数
     */
    @Schema(description = "分片总数")
    private Integer chunkTotal;
    /**
     * 分片在原文中的起始字符位置
     */
    @Schema(description = "分片在原文中的起始字符位置")
    private Integer startIndex;
    /**
     * 分片在原文中的结束字符位置
     */
    @Schema(description = "分片在原文中的结束字符位置")
    private Integer endIndex;

    /**
     * 生成向量的嵌入模型标识
     */
    @Schema(description = "生成向量的嵌入模型标识")
    private String embeddingModel;
    /**
     * 向量文档ID
     */
    @Schema(description = "向量文档ID")
    private String vectorDocId;
    /**
     * 是否已向量化入库
     */
    @Schema(description = "是否已向量化入库")
    private Integer vectorized;
    /**
     * 元数据
     */
    @Schema(description = "扩展元数据")
    private String metadata;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
