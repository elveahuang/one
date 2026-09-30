package cc.wdev.platform.system.config.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "配置参数保存请求")
public class ConfigSaveRequest implements Serializable {
    /**
     * ID
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;
    /**
     * 分组
     */
    @Schema(description = "分组")
    private String configGroupType;
    /**
     * 内容类型
     */
    @Schema(description = "内容类型")
    private String configContentType;
    /**
     * 参数名
     */
    @Schema(description = "参数名")
    private String configKey;
    /**
     * 参数值
     */
    @Schema(description = "参数值")
    @NotBlank(message = "参数值不能为空")
    private String configValue;
    /**
     * 默认值
     */
    @Schema(description = "默认值")
    private String defaultValue;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 帮助信息
     */
    @Schema(description = "帮助信息")
    private String help;
}
