package cc.wdev.platform.system.ai.domain.vo;

import cc.wdev.platform.commons.annotations.SensitiveMark;
import cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 模型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiModelVo implements Serializable {
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
     * 标题
     */
    @Schema(title = "名称")
    private String title;
    /**
     * 模型提供商
     */
    private String modelProvider;
    /**
     * 模型服务提供商
     */
    private String serviceProvider;
    /**
     * 模型类型
     */
    private String modelType;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * Api Key
     */
    @SensitiveMark(strategy = SensitiveMarkStrategy.API_KEY)
    private String apiKey;
    /**
     * 基础URL
     */
    private String baseUrl;
    /**
     * 参数配置
     */
    private String variables;
    /**
     * 备注说明
     */
    private String description;
    /**
     * 状态
     */
    private Integer status;
}
