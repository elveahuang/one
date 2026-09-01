package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 知识库
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_kb")
@Schema(title = "知识库", description = "知识库")
public class AiKbEntity extends BaseTenantEntity implements CodeEntity {
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 向量集合名称
     */
    @Schema(description = "向量集合名称")
    private String collectionName;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String details;
    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String description;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
    /**
     * 单次召回数量
     */
    @Schema(description = "单次召回数量")
    private Integer topK;
    /**
     * 相似度阈值
     */
    @Schema(description = "相似度阈值")
    private Double similarityThreshold;
    /**
     * 分片大小
     */
    @Schema(description = "分片大小")
    private Integer chunkSize;
    /**
     * 分片重叠
     */
    @Schema(description = "分片重叠")
    private Integer chunkOverlap;

    /**
     * 元数据
     */
    @Schema(description = "元数据")
    private String metadata;

}
