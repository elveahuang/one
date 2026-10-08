package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "智能体表单", description = "智能体表单")
public class AiAgentSaveRequest implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
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
    @Schema(title = "提示词", description = "系统提示此智能体的行为")
    private String systemPrompt;

    /**
     * 描述
     */
    @Schema(title = "备注说明", description = "备注说明")
    private String description;

    /**
     * 关联模型
     */
    @Schema(title = "关联模型", description = "关联模型")
    @NotNull(message = "关联模型不能为空")
    private Long modelId;

    /**
     * 关联知识库
     */
    @Schema(title = "关联知识库", description = "关联知识库")
    private Long kbId;

    /**
     * 关联工具
     */
    @Schema(title = "关联工具", description = "关联工具")
    private List<Long> toolIds;

    /**
     * 关联McpServer
     */
    @Schema(title = "关联McpServer", description = "关联McpServer")
    private List<Long> mcpServerIds;

    /**
     * 描述
     */
    @Schema(title = "描述", description = "描述")
    private String details;

    /**
     * AI问候语
     */
    @Schema(description = "AI问候语")
    private String greeting;

    /**
     * 启用状态
     */
    @Schema(title = "启用状态", description = "启用状态")
    private Integer status;
}
