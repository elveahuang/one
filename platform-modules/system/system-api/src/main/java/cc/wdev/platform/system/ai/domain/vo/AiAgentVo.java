package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能体VO")
public class AiAgentVo implements Serializable {
    /**
     * 主键
     */
    @Schema(title = "智能体ID", description = "智能体ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
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
     * 提示词
     */
    @Schema(title = "智能体提示词", description = "系统提示此智能体的行为")
    private String prompt;
    /**
     * 系统提示词
     */
    @Schema(title = "系统提示词", description = "系统提示词")
    private String systemPrompt;
    /**
     * 描述
     */
    @Schema(title = "描述", description = "描述")
    private String description;
    /**
     *
     */
    private String rolePrompt;
    /**
     *
     */
    private BigDecimal temperature;
    /**
     * 描述
     */
    private String details;
    /**
     *
     */
    private Integer status;
    /**
     * 关联模型
     */
    @Schema(title = "关联模型", description = "关联模型")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Long modelId;
    /**
     * 关联模型详情
     */
    private AiModelVo model;
    /**
     * 关联知识库
     */
    @Schema(title = "关联知识库", description = "关联知识库")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Long kbId;
    /**
     * 关联知识库详情
     */
    private AiKbVo kb;
    /**
     * 关联工具
     */
    @Schema(title = "关联工具", description = "关联工具")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> toolIds;
    /**
     * 关联工具
     */
    private List<String> toolNames;
    /**
     * 关联McpServer
     */
    @Schema(title = "关联McpServer", description = "关联McpServer")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> mcpServerIds;
    /**
     * 问候语
     */
    @Schema(description = "问候语")
    private String greeting;
}
