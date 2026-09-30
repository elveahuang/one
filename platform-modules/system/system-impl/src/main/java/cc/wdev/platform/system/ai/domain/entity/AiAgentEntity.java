package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * 智能体
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_agent")
@Schema(title = "智能体", description = "智能体")
public class AiAgentEntity extends BaseTenantEntity implements CodeEntity {
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String title;
    /**
     * 描述
     */
    private String details;
    /**
     * 问候语
     */
    @Schema(description = "问候语")
    private String greeting;
    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String prompt;
    /**
     * 角色提示词
     */
    private String rolePrompt;
    /**
     * 系统提示词
     */
    private String systemPrompt;
    /**
     *
     */
    private BigDecimal temperature;
    /**
     * 备注说明
     */
    private String description;
    /**
     * 状态
     */
    private Integer status;
}
