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
@TableName("sys_ai_kb_item")
@Schema(title = "知识库知识条目", description = "知识库知识条目")
public class AiKbItemEntity extends BaseTenantEntity {
    /**
     * 知识库ID
     */
    @Schema(description = "知识库ID")
    private Long kbId;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private Long bizId;
    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 问题
     */
    @Schema(description = "问题")
    private String question;
    /**
     * 答案
     */
    @Schema(description = "答案")
    private String answer;
    /**
     * 原始内容
     */
    @Schema(description = "原始内容")
    private String content;
    /**
     * 内容类型
     */
    @Schema(description = "内容类型")
    private String contentType;
    /**
     * 内容哈希，用于去重或变更检测
     */
    @Schema(description = "内容哈希，用于去重或变更检测")
    private String contentHash;
    /**
     * 内容大小
     */
    @Schema(description = "内容大小")
    private Long contentSize;
    /**
     * 扩展元数据
     */
    @Schema(description = "扩展元数据")
    private String metadata;
    /**
     * 附加数据，跟bizType配合用于处理其他来源文档的向量化
     */
    @Schema(description = "扩展元数据")
    private String extra;

    /**
     * 分片策略（TOKEN / RECURSIVE / MARKDOWN），为空使用知识库/全局配置
     */
    @Schema(description = "分片策略")
    private String chunkStrategy;
    /**
     * 是否已向量化
     */
    @Schema(description = "是否已向量化")
    private Integer vectorized;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
