package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 模型
 * 前端接口使用，不包含密钥等私密信息
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "模型")
public class AiModelSimpleVo implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(title = "ID")
    private Long id;
    /**
     * 编号
     */
    @Schema(title = "编号")
    private String code;
    /**
     * 名称
     */
    @Schema(title = "名称")
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
    @Schema(title = "模型类型")
    private String modelType;
    /**
     * 模型名称
     */
    @Schema(title = "模型名称")
    private String modelName;
}
