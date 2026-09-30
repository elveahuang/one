package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * AI 用量统计
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_usage")
@Schema(title = "AI 用量统计", description = "AI 用量统计")
public class AiUsageEntity extends BaseTenantEntity {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用量类型：CHAT / SEARCH / EMBEDDING / RERANK
     */
    @Schema(description = "用量类型")
    private String usageType;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 知识库ID
     */
    @Schema(description = "知识库ID")
    private Long kbId;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String conversationId;

    /**
     * 输入 token
     */
    @Schema(description = "输入 token")
    private Integer promptTokens;

    /**
     * 输出 token
     */
    @Schema(description = "输出 token")
    private Integer completionTokens;

    /**
     * 总 token
     */
    @Schema(description = "总 token")
    private Integer totalTokens;

    /**
     * 调用次数
     */
    @Schema(description = "调用次数")
    private Integer callCount;

}
