package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.ai.config.RetrievalConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 知识库保存请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "知识库表单", description = "知识库表单")
public class AiKbSaveRequest implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 向量模型ID
     */
    @Schema(title = "向量模型ID", description = "向量模型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long embeddingModelId;

    /**
     * 对话模型ID
     */
    @Schema(title = "对话模型ID", description = "对话模型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatModelId;

    /**
     * 重排模型ID
     */
    @Schema(title = "重排模型ID", description = "重排模型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long rerankModelId;

    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    @NotBlank(message = "知识库编号不能为空")
    private String code;

    /**
     * 名称
     */
    @Schema(title = "名称", description = "名称")
    @NotBlank(message = "知识库名称不能为空")
    private String title;

    /**
     * 向量集合名称（默认取编号）
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
     * 检索配置（不填则使用全局默认配置）
     */
    @Schema(title = "检索配置", description = "知识库级检索配置，覆盖全局 platform.ai.retrieval.*")
    private RetrievalConfig retrievalConfig;

    /**
     * 分片大小（token），0 表示使用全局切分器配置
     */
    @Schema(title = "分片大小", description = "分片大小（token），0 表示使用全局切分器配置")
    private Integer chunkSize;

    /**
     * 分片重叠（token），0 表示使用全局切分器配置
     */
    @Schema(title = "分片重叠", description = "分片重叠（token），0 表示使用全局切分器配置")
    private Integer chunkOverlap;

}
