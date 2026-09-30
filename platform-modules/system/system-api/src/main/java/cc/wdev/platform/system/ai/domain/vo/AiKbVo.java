package cc.wdev.platform.system.ai.domain.vo;

import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库VO
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库VO")
public class AiKbVo implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;

    /**
     * 名称
     */
    @Schema(title = "名称", description = "名称")
    private String title;

    /**
     * 向量集合名称
     */
    @Schema(title = "向量集合名称", description = "向量集合名称")
    private String collectionName;

    /**
     * 描述
     */
    @Schema(title = "描述", description = "描述")
    private String details;

    /**
     * 备注说明
     */
    @Schema(title = "备注说明", description = "备注说明")
    private String description;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间", description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    /**
     * 向量模型ID
     */
    @Schema(title = "向量模型ID", description = "向量模型ID")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Long embeddingModelId;

    /**
     * 向量模型详情
     */
    private AiModelVo embeddingModel;

    /**
     * 对话模型ID
     */
    @Schema(title = "对话模型ID", description = "对话模型ID")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Long chatModelId;

    /**
     * 对话模型详情
     */
    @Schema(title = "对话模型详情", description = "对话模型详情")
    private AiModelVo chatModel;

    /**
     * 重排模型ID
     */
    @Schema(title = "重排模型ID", description = "重排模型ID")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Long rerankModelId;

    /**
     * 重排模型详情
     */
    @Schema(title = "重排模型详情", description = "重排模型详情")
    private AiModelVo rerankModel;

    /**
     * 检索配置（知识库维度 topK / similarityThreshold 等）
     */
    @Schema(title = "检索配置", description = "检索配置")
    private RetrievalConfig retrievalConfig;

    /**
     * 分片大小（token）
     */
    @Schema(title = "分片大小")
    private Integer chunkSize;

    /**
     * 分片重叠（token）
     */
    @Schema(title = "分片重叠")
    private Integer chunkOverlap;

}
