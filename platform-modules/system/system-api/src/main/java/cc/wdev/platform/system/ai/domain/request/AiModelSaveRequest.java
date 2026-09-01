package cc.wdev.platform.system.ai.domain.request;

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
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "AI模型表单", description = "AI模型表单")
public class AiModelSaveRequest implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;

    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 模型提供商
     */
    @Schema(title = "模型提供商", description = "模型提供商")
    @NotBlank(message = "模型提供商不能为空")
    private String modelProvider;

    /**
     * 模型服务提供商
     */
    @Schema(title = "服务提供商", description = "服务提供商")
    @NotBlank(message = "服务提供商不能为空")
    private String serviceProvider;

    /**
     * 模型类型
     */
    @Schema(title = "模型类型", description = "模型类型")
    @NotBlank(message = "模型类型不能为空")
    private String modelType;

    /**
     * 模型名称
     */
    @Schema(title = "模型名称", description = "模型名称")
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    /**
     * API密钥
     */
    @Schema(title = "API密钥", description = "API密钥")
    @NotBlank(message = "API密钥不能为空")
    private String apiKey;

    /**
     * 基础URL
     */
    @Schema(title = "基础URL", description = "基础URL")
    private String baseUrl;

    /**
     * 参数配置
     */
    @Schema(title = "参数配置", description = "参数配置")
    private String variables;

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
}
