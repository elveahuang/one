package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI模型查询请求")
public class AiModelSearchRequest extends PageRequest {
    /**
     * 模型名称
     */
    @Schema(title = "模型名称", description = "模型名称")
    private String modelName;
    /**
     * 模型提供商
     */
    private String modelProvider;
    /**
     * 服务提供商
     */
    private String serviceProvider;
    /**
     * 模型类型
     */
    @Schema(title = "模型类型", description = "模型类型")
    private String modelType;
}
